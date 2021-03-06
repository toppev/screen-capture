package dev.toppe.img.screencapture;

import dev.toppe.img.screencapture.editor.EditorPanel;
import dev.toppe.img.screencapture.settings.SettingsFile;
import dev.toppe.img.screencapture.settings.SettingsFrame;
import dev.toppe.img.screencapture.uploader.UploadLink;
import dev.toppe.img.screencapture.uploader.Uploader;
import dev.toppe.img.screencapture.util.ImageUtil;
import dev.toppe.img.screencapture.screenshot.Screenshot;
import dev.toppe.img.screencapture.uploader.UploaderProvider;
import dev.toppe.img.screencapture.userhelp.HelpButton;
import dev.toppe.img.screencapture.util.DesktopUtil;
import dev.toppe.img.screencapture.util.TransferableImage;

import javax.swing.*;
import java.awt.*;
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
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScreenCapture extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = 8905339237280128760L;
    private static final Logger logger = Logger.getLogger(ScreenCapture.class.getSimpleName());
    private static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    static final Clipboard CLIPBOARD = toolkit.getSystemClipboard();
    private static final Dimension dimension = new Dimension(700, 500);

    private Screenshot screenshot;
    private EditorPanel editor;

    private JButton uploadButton;
    private JButton captureButton;
    private JButton blankImageButton;

    private UploadLink url;
    private boolean uploading;

    private SettingsFile settingsFile;


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
        settingsFile = new SettingsFile();
        CompletableFuture.runAsync(settingsFile::load);
    }

    public static void main(String[] args) {
        logger.info("Starting screen-capture...");
        SwingUtilities.invokeLater(ScreenCapture::new);
    }

    public static Logger getLogger() {
        return logger;
    }

    /**
     * Creates and adds screenshot and upload buttons
     */
    private void createButtons() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        panel.setPreferredSize(new Dimension(150, 500));

        captureButton = new JButton();
        captureButton.setText("New Screenshot");
        captureButton.addActionListener(this);
        captureButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        c.insets = new Insets(20, 10, 20, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 25;
        panel.add(captureButton, c);

        uploadButton = new JButton();
        uploadButton.setText("Upload Clipboard");
        uploadButton.addActionListener(this);
        uploadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        uploadButton.setEnabled(getClipboardImage() != null);
        c.gridy = 1;
        panel.add(uploadButton, c);

        blankImageButton = new JButton();
        blankImageButton.setText("Blank Image");
        blankImageButton.addActionListener(this);
        blankImageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        c.gridy++;
        panel.add(blankImageButton, c);
        c.gridy++;
        panel.add(new SettingsFrame.SettingsButton(this), c);
        c.gridy++;
        panel.add(new HelpButton(this), c);
        c.gridy++;

        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.WEST);

        // open blank image
        updateEditor();
        this.add(editor, BorderLayout.EAST);
    }

    /**
     * Uploads the image from clipboard
     */
    public void upload() {
        if (editor != null) {
            this.remove(editor);
            if (editor.getImage() != null) {
                uploading = true;
                updateButtons();
                new Thread(() -> {
                    Uploader uploader = UploaderProvider.getUploader();
                    url = uploader.upload(editor.getImage());
                    if (url == null) {
                        logger.log(Level.SEVERE, "Failed to upload image.");
                        return;
                    }
                    uploading = false;
                    // Copy the url to the image, not the website
                    StringSelection selection = new StringSelection(url.getImageLink());
                    CLIPBOARD.setContents(selection, null);
                    SwingUtilities.invokeLater(() -> {
                        updateButtons();
                        uploadDoneDialog();
                    });
                }).start();
            }
        }
    }

    private void uploadDoneDialog() {
        if (url != null) {
            String[] options = {"Open In Browser"};
            int response = JOptionPane.showOptionDialog(null, "Link copied to clipboard", "Upload success!", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (response == 0) {
                // Open the website, not the image
                DesktopUtil.openInBrowser(url.getWebLink());
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
    public BufferedImage getClipboardImage() {
        BufferedImage image = null;
        DataFlavor flavor = DataFlavor.imageFlavor;
        if (CLIPBOARD.isDataFlavorAvailable(flavor)) {
            try {
                image = (BufferedImage) CLIPBOARD.getData(flavor);
            } catch (UnsupportedFlavorException | IOException e1) {
                logger.log(Level.SEVERE, "Failed to get from clipboard", e1);
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
            } else if (src == captureButton) {
                newScreenshot();
            } else if (src == blankImageButton) {
                new BlankImageFrame(this);
            }
        }
    }

    public Screenshot newScreenshot() {
        if (screenshot != null) {
            screenshot.dispose();
        }
        this.setVisible(false);
        screenshot = new Screenshot(image -> {
            ScreenCapture.this.setVisible(true);
            if (image != null) {
                CLIPBOARD.setContents(new TransferableImage(image), null);
                updateEditor();
            }
        });
        return screenshot;
    }

    /**
     * updates/creates EditorPanel with clipboard image or blank image
     */
    public void updateEditor() {
        // get the clipboard image or if null return new blank image
        BufferedImage image = getClipboardImage() == null ? ImageUtil.blankImage(600, 600) : getClipboardImage();
        updateEditor(image);
    }

    public void updateEditor(BufferedImage image) {
        // update image in current editor
        if (editor != null) {
            editor.updateImage(image);
        }
        // open new editor
        else {
            editor = new EditorPanel(image, ScreenCapture.this);
            repaint();
            updateButtons();
        }
    }

    /**
     * Gets the current image in editor or if null then clipboard image
     *
     * @return
     */
    public BufferedImage getImage() {
        // try to get the current image from image editor
        if (editor != null && editor.getImage() != null) {
            return editor.getImage();
        }
        // get the clipboard image
        return getClipboardImage();
    }

    @Override
    public void windowOpened(WindowEvent arg0) {
        // No need to do anything
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
        // No need to do anything
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
        // No need to do anything
    }

    @Override
    public void windowIconified(WindowEvent arg0) {
        // No need to do anything
    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {
        updateButtons();
    }

    @Override
    public void windowActivated(WindowEvent arg0) {
        updateButtons();
    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {
        // No need to do anything
    }

    public EditorPanel getEditor() {
        return editor;
    }

    public SettingsFile getSettingsFile() {
        return settingsFile;
    }
}
