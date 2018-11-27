package com.gmail.thetoppe5.screencapture.screenshot;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.gmail.thetoppe5.screencapture.util.SoundPlayer;

public class ScreenshotThread extends Thread {

    private final JPanel panel;
    private final long wait;
    private final ScreenshotCallback callback;
    private final JFrame frame;

    public ScreenshotThread(JFrame frame, JPanel panel, long wait, ScreenshotCallback callback) {
        this.frame = frame;
        this.panel = panel;
        this.wait = wait;
        this.callback = callback;
        ;
    }

    /**
     * Takes the actual screenshot
     */
    @Override
    public void run() {
        Point point = panel.getLocationOnScreen();
        frame.setVisible(false);
        frame.repaint();
        //select the rectangle
        Rectangle rec = new Rectangle(point.x, point.y, panel.getWidth(), panel.getHeight());
        try {
            //wait so the frame will be completely invisible and not included in the image
            Thread.sleep(wait);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        try {
            //take the screenshot
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(rec);
            callback.onSuccess(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
