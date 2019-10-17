package dev.toppe.img.screencapture.screenshot;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JPanel;

import dev.toppe.img.screencapture.ScreenCapture;

public class ScreenshotThread extends Thread {

    private final JPanel panel;
    private final long wait;
    private final Consumer<Image> callback;
    private final JFrame frame;

    public ScreenshotThread(JFrame frame, JPanel panel, long wait, Consumer<Image> callback) {
        this.frame = frame;
        this.panel = panel;
        this.wait = wait;
        this.callback = callback;
    }

    /**
     * Takes the actual screenshot
     */
    @Override
    public void run() {
        Point point = panel.getLocationOnScreen();
        frame.setVisible(false);
        frame.repaint();
        // select the rectangle
        Rectangle rec = new Rectangle(point.x, point.y, panel.getWidth(), panel.getHeight());
        try {
            // wait so the frame will be completely invisible and not included in the image
            Thread.sleep(wait);
        } catch (InterruptedException e1) {
            ScreenCapture.getLogger().log(Level.SEVERE, "ScreenshotThread was interrupted", e1);
            Thread.currentThread().interrupt();
        }
        try {
            // take the screenshot
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(rec);
            callback.accept(image);
        } catch (Exception e) {
            ScreenCapture.getLogger().log(Level.SEVERE, "Failed to take screenshot", e);
        }
    }

}
