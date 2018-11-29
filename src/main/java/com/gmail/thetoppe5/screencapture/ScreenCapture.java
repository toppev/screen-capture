package com.gmail.thetoppe5.screencapture;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.gmail.thetoppe5.screencapture.editor.EditorPanel;
import com.gmail.thetoppe5.screencapture.screenshot.Screenshot;
import com.gmail.thetoppe5.screencapture.screenshot.ScreenshotCallback;
import com.gmail.thetoppe5.screencapture.uploader.IUploader;
import com.gmail.thetoppe5.screencapture.uploader.UploadProvider;
import com.gmail.thetoppe5.screencapture.userhelp.HelpButton;
import com.gmail.thetoppe5.screencapture.util.TransferableImage;

public class ScreenCapture extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1L;

    public static final Toolkit toolkit = Toolkit.getDefaultToolkit();

    public static final Clipboard CLIPBOARD = toolkit.getSystemClipboard();

    private final Dimension dimension = new Dimension(700, 500);

    private JPanel panel;

    private Screenshot screenshot;
    private EditorPanel editor;

    private JButton uploadButton;
    private JButton captureButton;

    private String url;
    private boolean uploading;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new ScreenCapture();
            }
        });
    }

    /**
     * Initializes everything and builds the window
     */
    public ScreenCapture() {
        this.setTitle("Screen Capture");
        this.setSize(dimension);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(this);

        // register hotkeys
        new HotKeyListener(this).register();

        createButtons();

        setVisible(true);
        updateButtons();

    }

    /**
     * Creates and adds screenshot and upload buttons
     */
    private void createButtons() {
        panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        panel.setPreferredSize(new Dimension(150, 500));

        captureButton = new JButton();
        captureButton.setText("New Screenshot");
        captureButton.addActionListener(this);
        captureButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        c.insets = new Insets(25, 10, 25, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        // size of the buttons
        c.ipady = 40;
        panel.add(captureButton, c);

        uploadButton = new JButton();
        uploadButton.setText("Upload Clipboard");
        uploadButton.addActionListener(this);
        uploadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        uploadButton.setEnabled(getImage() != null);
        c.gridy = 3;
        panel.add(uploadButton, c);
        
        //add help button
        c.gridy = 5;
        panel.add(new HelpButton(this), c);

        // layout for this JFrame
        this.setLayout(new BorderLayout());
        // add buttons panel to the JFrame
        this.add(panel, BorderLayout.WEST);

        // open blank image
        updateEditor();

        // add it to this JFrame
        this.add(editor, BorderLayout.EAST);
    }

    /**
     * Uploads the image from clipboard
     */
    public void upload() {
        if (editor != null) {
            this.remove(editor);
        }
        if (getImage() != null) {
            uploading = true;
            updateButtons();
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    IUploader uploader = UploadProvider.getProvider();
                    String url = uploader.upload(getImage());
                    if (url != null) {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                ScreenCapture.this.url = url;
                                uploading = false;
                                StringSelection selection = new StringSelection(url);
                                CLIPBOARD.setContents(selection, null);
                                updateButtons();
                                uploadDoneDialog();
                            }
                        });
                    } else {
                        // "Failed to upload image. Try again.");
                    }
                }
            });
            thread.start();
        }
    }

    private void uploadDoneDialog() {
        if (url != null) {
            String[] options = new String[] { "Open In Browser" };
            int response = JOptionPane.showOptionDialog(null, "Link copied to clipboard", "Upload success!",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (response == 0) {
                openInBrowser();
            }
        }
    }

    /**
     * Updates the main window buttons
     */
    private void updateButtons() {
        // currently only thissss
        uploadButton.setEnabled(!uploading);
    }

    /**
     * Gets the image from clipboard
     * 
     * @return image from clipboard
     */
    public BufferedImage getImage() {
        BufferedImage image = null;
        DataFlavor flavor = DataFlavor.imageFlavor;
        if (CLIPBOARD.isDataFlavorAvailable(flavor)) {
            try {
                image = (BufferedImage) CLIPBOARD.getData(flavor);
            } catch (UnsupportedFlavorException | IOException e1) {
                e1.printStackTrace();
            }
        }
        return image != null ? image : blankImage(500, 500);
    }

    private BufferedImage blankImage(int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, width, height);
        return bi;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src != null) {
            if (src == uploadButton) {
                upload();
            } else if (src == captureButton) {
                newScreenshot();
            }
        }
    }

    public Screenshot newScreenshot() {
        if (screenshot != null) {
            screenshot.dispose();
        }
        this.setVisible(false);
        screenshot = new Screenshot(new ScreenshotCallback() {

            @Override
            public void onSuccess(BufferedImage image) {
                ScreenCapture.this.setVisible(true);
                if (image != null) {
                    CLIPBOARD.setContents(new TransferableImage(image), null);
                    updateEditor();
                }
            }

            @Override
            public void onFailure(Exception e) {
                ScreenCapture.this.setVisible(true);
                e.printStackTrace();
            }
        });
        return screenshot;
    }

    /**
     * Creates a new image editor
     */
    private void updateEditor() {
        // update image in current editor
        if (editor != null) {
            editor.updateImage(getImage());
        }
        // open new editor
        else {
            editor = new EditorPanel(getImage(), ScreenCapture.this);
            repaint();
            updateButtons();
        }
    }

    /**
     * Opens the image url in browser
     */
    private void openInBrowser() {
        if (url != null) {
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(url));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    public void windowActivated(WindowEvent arg0) {
        updateButtons();
    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {
        updateButtons();
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {
    }

    @Override
    public void windowIconified(WindowEvent arg0) {
    }

    @Override
    public void windowOpened(WindowEvent arg0) {
    }

    public Screenshot getScreenshot() {
        return screenshot;
    }

}
