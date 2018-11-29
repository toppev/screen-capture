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

    private JLabel widthLabel;
    private JLabel heightLabel;

    private JSpinner widthSpinner;
    private JSpinner heightSpinner;

    private JButton colorButton;

    private JButton ok;
    private JButton cancel;

    private JColorChooser colorChooser;

    public BlankImageFrame(ScreenCapture host) {
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
        widthSpinner = new JSpinner(new SpinnerNumberModel(500, 10, 10000, 1));
        this.add(widthSpinner, c);
        c.gridy = 1;
        heightSpinner = new JSpinner(new SpinnerNumberModel(500, 10, 10000, 1));
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
        cancel.addActionListener(l -> this.dispose());
        this.add(cancel, c);

        c.gridx = 2;
        ok = new JButton("OK");
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
