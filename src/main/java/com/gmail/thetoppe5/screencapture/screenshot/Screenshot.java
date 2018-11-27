package com.gmail.thetoppe5.screencapture.screenshot;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import com.gmail.thetoppe5.screencapture.util.SoundPlayer;

public class Screenshot extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static Point lastLocation;

    private JPanel panel;
    private JButton button;
    private ScreenshotCallback callback;

    public Screenshot(ScreenshotCallback callback) {
        this.setTitle("Screen Capturer");
        this.setSize(400, 400);
        this.setLocationRelativeTo(null);
        this.setUndecorated(true);
        this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        this.setBackground(new Color(0, 0, 0, 0));
        this.setAlwaysOnTop(true);
        
        this.callback = callback;

        panel = new JPanel();
        panel.setOpaque(false);

        button = new JButton("Screenshot");
        button.setSize(20, 10);
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                takeScreenshot();
            }
        });
        panel.add(button);
        this.add(panel);
        this.setVisible(true);
    }
    
    public void takeScreenshot() {
        long wait = 150;
        //play the sound async
        playSoundEffectAsync();
        new ScreenshotThread(Screenshot.this, panel, wait, callback).start();
        lastLocation = Screenshot.this.getLocation();
    }
    
    
    /**
     * Plays the sound effect asynchronously
     */
    private void playSoundEffectAsync() {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                SoundPlayer.playScreenshotSound();
            }
        }).start();
    }

    public static Point getLastLocation() {
        return lastLocation;
    }
}
