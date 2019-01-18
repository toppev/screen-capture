package com.gmail.thetoppe5.screencapture.editor;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EditorTextField extends JTextField
        implements ActionListener, FocusListener, MouseListener, DocumentListener {

    private static final long serialVersionUID = 1L;

    private EditorPanel editor;

    public EditorTextField(EditorPanel editorPanel) {
        this.editor = editorPanel;
        setOpaque(false);
        setColumns(1);
        setSize(getPreferredSize());
        setColumns(0);
        addFocusListener(this);
        addMouseListener(this);
        this.getDocument().addDocumentListener(this);
        addActionListener(this);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        setSize(getPreferredSize());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        setSize(getPreferredSize());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            setEditable(true);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        setEditable(false);
        
        Graphics2D g = editor.createGraphics();
        
        //TODO the x y etc are not correct
        int x = getX();
        int y = getY();
        
        BufferedImage subImage = editor.backupImage.getSubimage(x, y, getWidth(), getHeight());
        g.drawImage(subImage, x, y, getWidth(), getHeight(), editor);

        g.setFont(getFont());
        g.drawString(getText(), x, y);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        setEditable(false);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

}
