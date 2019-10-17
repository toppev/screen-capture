package dev.toppe.img.screencapture;

import dev.toppe.img.screencapture.util.TransferableImage;
import dev.toppe.img.screencapture.screenshot.Screenshot;

import javax.imageio.ImageIO;
import javax.swing.FocusManager;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public class HotKeyListener {

    private ScreenCapture screenCapture;

    public HotKeyListener(ScreenCapture screenCapture) {
        this.screenCapture = screenCapture;
    }

    public void register() {
        // ESCAPE - quit
        // CTRL + N - new screenshot
        // CTRL + A - select full screen
        // CTRL + U - upload
        // CTRL + S - save to disk
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            // ignore key release
            if (e.getID() != KeyEvent.KEY_PRESSED) {
                return false;
            }
            // is the main frame
            if (FocusManager.getCurrentManager().getActiveWindow() != screenCapture) {
                return false;
            }
            int key = e.getKeyCode();
            boolean ctrl = e.isControlDown();
            // import from disk
            if (ctrl && key == KeyEvent.VK_I) {
                JFileChooser importer = new JFileChooser();
                importer.setFileFilter(new FileNameExtensionFilter("Files supported by ImageIO", ImageIO.getReaderFileSuffixes()));
                if (importer.showOpenDialog(screenCapture) == JFileChooser.APPROVE_OPTION) {
                    File file = importer.getSelectedFile();
                    try {
                        ScreenCapture.CLIPBOARD.setContents(new TransferableImage(ImageIO.read(file)), null);
                        screenCapture.updateEditor();
                    } catch (IOException e1) {
                        ScreenCapture.getLogger().log(Level.SEVERE, "Couldn't load given image: " + file.getPath(), e1);
                    }
                }
            }
            // quit
            if (key == KeyEvent.VK_ESCAPE) {
                screenCapture.dispose();
                System.exit(0);
                return true;
            }
            // open screenshot frame
            if (ctrl && key == KeyEvent.VK_N) {
                screenCapture.newScreenshot();
                return true;
            }
            // make the frame "fullscreen" and take a screenshot
            if (ctrl && key == KeyEvent.VK_A) {
                Screenshot ss = screenCapture.newScreenshot();
                ss.setVisible(true);
                ss.setExtendedState(JFrame.MAXIMIZED_BOTH);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ss.takeScreenshot();
                    }
                }, 500);
                return true;
            }
            // try to upload
            if (ctrl && key == KeyEvent.VK_U) {
                screenCapture.upload();
                return true;
            }
            // save as
            if (ctrl && key == KeyEvent.VK_S) {
                BufferedImage image = screenCapture.getImage();
                if (image != null) {
                    JFileChooser saver = new JFileChooser();
                    if (saver.showSaveDialog(screenCapture) == JFileChooser.APPROVE_OPTION) {
                        File file = saver.getSelectedFile();
                        // default format to use
                        String format = "png";

                        String[] s = file.getName().split("\\.");
                        if (s.length > 0 && s[s.length - 1].length() > 0) {
                            format = s[s.length - 1];
                        } else {
                            file = new File(file.getPath() + "." + format);
                        }
                        ScreenCapture.getLogger().log(Level.INFO, "Saving in format: {0}", format);
                        try {
                            ImageIO.write(image, format, file);
                        } catch (IOException e1) {
                            ScreenCapture.getLogger().log(Level.SEVERE, "Failed to save image", e1);
                        }
                    }
                    return true;
                }
            }
            return false;
        });
    }

}
