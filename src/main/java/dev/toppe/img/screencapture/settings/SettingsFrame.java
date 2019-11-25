package dev.toppe.img.screencapture.settings;

import dev.toppe.img.screencapture.ScreenCapture;
import dev.toppe.img.screencapture.uploader.Uploader;
import dev.toppe.img.screencapture.uploader.UploaderProvider;
import dev.toppe.img.screencapture.util.DesktopUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class SettingsFrame extends JDialog {


    // "Width:" and "Height:" labels
    private JTextField tokenField;
    private JComboBox<Uploader> uploaderSelector;

    // ok (to create a new background) and cancel buttons
    private JButton ok;
    private JButton cancel;

    public void create(ScreenCapture host) {
        // create the frame and buttons et
        this.setSize(400, 250);
        this.setLocationRelativeTo(host);
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(15, 5, 5, 5);
        this.add(new JLabel("Uploader"), c);
        c.gridx = 1;
        uploaderSelector = new JComboBox<>(UploaderProvider.getUploaders());
        uploaderSelector.setSelectedItem(UploaderProvider.getUploader());
        System.out.println(UploaderProvider.getUploader());
        // Render the Class#getSimpleName()
        uploaderSelector.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value.getClass().getSimpleName());
                return this;
            }
        });
        uploaderSelector.addActionListener(a -> {
            Object obj = uploaderSelector.getSelectedItem();
            tokenField.setText(((Uploader)obj).getToken());
            ScreenCapture.getLogger().info("Selected uploader: " + obj.getClass().getName());
        });
        add(uploaderSelector, c);

        c.gridy = 2;
        c.gridx = 0;
        this.add(new JLabel("Token:"), c);
        c.gridx = 1;
        tokenField = new JTextField(UploaderProvider.getUploader().getToken());
        tokenField.setColumns(12);
        this.add(tokenField, c);

        c.gridx = 1;
        c.gridy = 4;
        JButton history = new JButton("Browse Token History");
        history.setMargin(new Insets(1, 2, 1, 2));
        this.add(history, c);
        history.setEnabled(((Uploader) uploaderSelector.getSelectedItem()).isBrowseHistory());
        uploaderSelector.addActionListener(l -> {
            history.setEnabled(((Uploader) uploaderSelector.getSelectedItem()).isBrowseHistory());
        });
        history.addActionListener(l -> {
            DesktopUtil.openInBrowser(((Uploader) uploaderSelector.getSelectedItem()).getBrowseHistoryURL());
        });

        c.gridx = 0;
        c.gridy = 5;
        cancel = new JButton("Cancel");
        // cancel does nothing else than disposes this frame
        cancel.addActionListener(l -> this.dispose());
        this.add(cancel, c);
        c.gridx = 1;
        ok = new JButton("OK");
        ok.addActionListener(e -> {
            Object obj = uploaderSelector.getSelectedItem();
            if (obj != null) {
                UploaderProvider.setUploader((Uploader) obj);
                UploaderProvider.getUploader().setToken(tokenField.getText());
                ScreenCapture.getLogger().info("Saved uploader: " + obj.getClass().getName());
            }
            SettingsFrame.this.dispose();
            host.getSettingsFile().save();
        });
        this.add(ok, c);
        this.setVisible(true);
    }

    public static class SettingsButton extends JButton {


        public SettingsButton(ScreenCapture host) {
            setText("Settings");
            addActionListener(e -> new SettingsFrame().create(host));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }
}