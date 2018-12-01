package com.gmail.thetoppe5.screencapture;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.gmail.thetoppe5.screencapture.util.ImageUtil;

public class BlankImageFrame extends JDialog {

    private static final long serialVersionUID = 1L;

    // "Width:" and "Height:" labels
    private JLabel widthLabel;
    private JLabel heightLabel;

    // Spinners where height and width are entered
    private JSpinner widthSpinner;
    private JSpinner heightSpinner;

    // button for selecting background color
    private JButton colorButton;

    // the actual color chooser
    private JColorChooser colorChooser;

    // ok (to create a new background) and cancel buttons
    private JButton ok;
    private JButton cancel;

    /**
     * Create a new BlankImageFrame to create a new background with one color
     * 
     * @param host
     *            the ScreenCapture instance, used to update the editor
     */
    public BlankImageFrame(ScreenCapture host) {
        // create the frame and buttons etc
        this.setSize(400, 200);
        this.setLocationRelativeTo(host);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 5, 0, 20);

        widthLabel = new JLabel("Width:");
        this.add(widthLabel, c);
        c.gridy = 1;
        heightLabel = new JLabel("Height:");
        this.add(heightLabel, c);

        c.gridy = 0;

        c.gridx = 1;
        // min width/heigh is 10
        // default is 500
        // max 2000
        // clicking adjusts +-1
        widthSpinner = new JSpinner(new SpinnerNumberModel(500, 10, 2000, 1));
        this.add(widthSpinner, c);
        c.gridy = 1;
        heightSpinner = new JSpinner(new SpinnerNumberModel(500, 10, 2000, 1));
        this.add(heightSpinner, c);

        colorChooser = new JColorChooser(Color.WHITE);
        // change color of the colorButton when changing color in the JColorChooser
        colorChooser.getSelectionModel().addChangeListener(l -> colorButton.setBackground(colorChooser.getColor()));

        colorButton = new JButton("Background");
        colorButton.setBackground(colorChooser.getColor());
        colorButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        // TODO make good size
        c.gridy = 2;
        c.gridx = 4;
        colorButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // open the color chooser
                JFrame colorChooserFrame = new JFrame();
                colorChooserFrame.setLocationRelativeTo(BlankImageFrame.this);
                colorChooserFrame.setSize(colorChooser.getPreferredSize());
                colorChooserFrame.add(colorChooser);
                colorChooser.setLocation(BlankImageFrame.this.getLocation());
                colorChooserFrame.setVisible(true);
                colorChooser.setVisible(true);
            }
        });
        this.add(colorButton, c);
        c.gridy = 4;
        c.gridx = 1;
        cancel = new JButton("Cancel");
        // cancel does nothing else than disposes this frame
        cancel.addActionListener(l -> this.dispose());
        this.add(cancel, c);

        c.gridx = 2;
        ok = new JButton("OK");
        // when pressed the blank image updates
        ok.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                host.updateEditor(ImageUtil.blankImage((int) widthSpinner.getValue(), (int) heightSpinner.getValue(),
                        colorChooser.getColor()));
                BlankImageFrame.this.dispose();
            }
        });
        this.add(ok, c);
        this.setVisible(true);
    }
}
