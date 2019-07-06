package com.gmail.thetoppe5.screencapture.userhelp;

import java.awt.Cursor;

import javax.swing.JButton;
import javax.swing.JFrame;

public class HelpButton extends JButton {

    private static final long serialVersionUID = 448385310125953281L;

    // TODO add icon
    /**
     * Creates a new HelpButton
     * 
     * @param frame
     *            the frame that should be the parent frame of the frame that opens
     *            when clicking this button
     */
    public HelpButton(JFrame frame) {
        setText("Help");
        addActionListener(e -> new HelpFrame(frame).create());
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

}
