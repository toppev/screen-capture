package com.gmail.thetoppe5.screencapture;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

import com.gmail.thetoppe5.screencapture.editor.EditImage;
import com.gmail.thetoppe5.screencapture.screenshot.Screenshot;
import com.gmail.thetoppe5.screencapture.screenshot.ScreenshotCallback;
import com.gmail.thetoppe5.screencapture.uploader.IUploader;
import com.gmail.thetoppe5.screencapture.uploader.UploadProvider;
import com.gmail.thetoppe5.screencapture.util.TransferableImage;

public class ScreenCapture extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1L;
    private static final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();

    private final Dimension dimension = new Dimension(200, 400);

    private JPanel panel;

    private EditImage editor;

    private JButton uploadButton;
    private JButton captureButton;
    private JButton previewButton;

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

        panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        captureButton = new JButton();
        captureButton.setText("New Screenshot");
        captureButton.addActionListener(this);
        captureButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_START;
        c.ipady = 0;
        c.weighty = 1.0;
        c.ipady = 40;
        c.gridx = 0;
        c.gridy = 0;

        panel.add(captureButton, c);

        uploadButton = new JButton();
        uploadButton.setText("Upload Clipboard");
        uploadButton.addActionListener(this);
        uploadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        uploadButton.setEnabled(getImage() != null);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridy++;
        c.gridx = 0;
        panel.add(uploadButton, c);

        previewButton = new JButton();
        previewButton.setText("Preview");
        previewButton.addActionListener(this);
        previewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridy++;
        panel.add(previewButton, c);

        this.add(panel);
        
        setVisible(true);
        updateButtons();

    }
    

    /**
     * Uploads the image from clipboard
     */
    private void upload() {
        if (editor != null) {
            // TODO editor.dispose();
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
                        //"Failed to upload image. Try again.");
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
        uploadButton.setEnabled(!uploading);
    }

    /**
     * Gets the image from clipboard
     * 
     * @return image from clipboard
     */
    private BufferedImage getImage() {
        BufferedImage image = null;
        DataFlavor flavor = DataFlavor.imageFlavor;
        if (CLIPBOARD.isDataFlavorAvailable(flavor)) {
            try {
                image = (BufferedImage) CLIPBOARD.getData(flavor);
            } catch (UnsupportedFlavorException | IOException e1) {
                // TODO
                e1.printStackTrace();
            }
        }
        return image;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src != null) {
            if (src == uploadButton) {
                upload();
            } else if (src == previewButton) {
                BufferedImage image = getImage();
                if (image != null) {
                    // dispose previous editor if open
                    if (editor != null) {
                        // TODO editor.dispose();
                    }
                    editor = new EditImage(this, image);
                }
            } else if (src == captureButton) {
                new Screenshot(new ScreenshotCallback() {

                    @Override
                    public void onSuccess(BufferedImage image) {
                        if (image != null) {
                            CLIPBOARD.setContents(new TransferableImage(image), null);
                            // dispose previous editor if open
                            if (editor != null) {
                                // TODO editor.dispose();
                            }
                            editor = new EditImage(ScreenCapture.this, image);
                            updateButtons();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    /**
     * Opens the uploaded image in browser
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

    public Dimension getStartingDimension() {
        return dimension;
    }

}
