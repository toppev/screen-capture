package com.gmail.thetoppe5.screencapture.screenshot;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;

public class Screenshot extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static Point lastLocation;

    private JPanel panel;
    private JButton button;

    public Screenshot(ScreenshotCallback callback) {
        this.setTitle("Screen Capturer");
        this.setSize(400, 400);
        this.setLocationRelativeTo(null);
        this.setUndecorated(true);
        this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        this.setBackground(new Color(0, 0, 0, 0));
        this.setAlwaysOnTop(true);

        panel = new JPanel();
        panel.setOpaque(false);

        button = new JButton("Screenshot");
        button.setSize(20, 10);
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                long wait = 150;
                new ScreenshotThread(Screenshot.this, panel, wait, callback).start();
                lastLocation = Screenshot.this.getLocation();
            }
        });
        panel.add(button);
        this.add(panel);
        this.setVisible(true);
    }

    public static Point getLastLocation() {
        return lastLocation;
    }
}
