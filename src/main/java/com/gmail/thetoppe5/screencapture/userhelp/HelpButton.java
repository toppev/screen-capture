package com.gmail.thetoppe5.screencapture.userhelp;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class HelpButton extends JButton {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // TODO add icon
    public HelpButton(JFrame frame) {
        setText("Help");
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new HelpFrame(frame).create();
            }
        });
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

}
