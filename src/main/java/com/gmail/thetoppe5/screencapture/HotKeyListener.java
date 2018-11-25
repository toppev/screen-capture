package com.gmail.thetoppe5.screencapture;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class HotKeyListener {

    private ScreenCapture screenCapture;

    public HotKeyListener(ScreenCapture screenCapture) {
        this.screenCapture = screenCapture;
    }

    public void register() {
        /**
         * ESCAPE - quit.
         * CTRL + N - new screenshot
         * CTRL + A - select full screen
         * CTRL + U - upload.
         * CTRL + S - save to disk.
         */
        
        //TODO open image from disk (?)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() != KeyEvent.KEY_PRESSED) {
                    return false;
                }
                int key = e.getKeyCode();
                boolean ctrl = e.isControlDown();
                //quit
                if (key == KeyEvent.VK_ESCAPE) {
                    screenCapture.dispose();
                    System.exit(0);
                    return true;
                }
                //open screenshot frame
                if (ctrl && key == KeyEvent.VK_N) {
                    screenCapture.newScreenshot();
                    return true;
                }
                //
                if (ctrl && key == KeyEvent.VK_A) {

                    return true;
                }
                //try to upload
                if (ctrl && key == KeyEvent.VK_U) {
                    screenCapture.upload();
                    return true;
                }
                //
                if (ctrl && key == KeyEvent.VK_S) {
                    BufferedImage image = screenCapture.getImage();
                    if(image != null) {
                        JFileChooser saver = new JFileChooser();
                        if(saver.showSaveDialog(screenCapture) == JFileChooser.APPROVE_OPTION) {
                            File file = saver.getSelectedFile();
                            try {
                                ImageIO.write(image, "png", file);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

}
