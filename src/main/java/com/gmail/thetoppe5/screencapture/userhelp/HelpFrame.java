package com.gmail.thetoppe5.screencapture.userhelp;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class HelpFrame {

    private final JFrame parent;

    /**
     * Create a new HelpFrame instance. Call create() to actually create the JFrame
     * 
     * @param parent
     *            used for the help JDialog
     */
    public HelpFrame(JFrame parent) {
        this.parent = parent;
    }

    /**
     * Create the frame
     */
    public void create() {
        SwingUtilities.invokeLater(() -> {
            long keepOpen = TimeUnit.SECONDS.toMillis(20);
            JOptionPane help = new JOptionPane(
                    "Use mouse 1 to draw, mouse 2 to change color,"
                            + "mouse 3 to erase, scroll to change font size and double click to add text",
                    JOptionPane.INFORMATION_MESSAGE);
            JDialog msg = help.createDialog(parent, "How To Edit");
            msg.setModal(false);
            msg.setVisible(true);
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    msg.dispose();
                }
            }, keepOpen);
        });
    }
}
