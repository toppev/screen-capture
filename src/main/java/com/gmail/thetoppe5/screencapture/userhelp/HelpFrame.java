package com.gmail.thetoppe5.screencapture.userhelp;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class HelpFrame {

    private JFrame parent;

    public HelpFrame(JFrame parent) {
        this.parent = parent;
    }

    public void create() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                long keepOpen = 1000 * 20;

                JOptionPane help = new JOptionPane("Use mouse 1 to draw, mouse 2 to change color,"
                        + "mouse 3 to erase and scroll to change font size.", JOptionPane.INFORMATION_MESSAGE);
                JDialog msg = help.createDialog(parent, "How To Edit");
                msg.setModal(false);
                msg.setVisible(true);

                new Timer().schedule(new TimerTask() {

                    @Override
                    public void run() {
                        msg.dispose();
                    }
                }, keepOpen);

            }
        });
    }
}
