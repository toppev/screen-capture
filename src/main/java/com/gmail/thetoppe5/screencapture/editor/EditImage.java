package com.gmail.thetoppe5.screencapture.editor;

import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import com.gmail.thetoppe5.screencapture.ScreenCapture;

public class EditImage {
    
    
    
    private EditorPanel editorPanel;

    /**
     * Opens a new editor window with the image
     * 
     * @param bufferedImage
     *            image to edit
     */
    public EditImage(ScreenCapture screenCapture, BufferedImage bufferedImage) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                editorPanel = new EditorPanel(bufferedImage, screenCapture);
                screenCapture.setSize((int) (screenCapture.getStartingDimension().getWidth() + 500), 500);
                screenCapture.add(editorPanel);
            }
        });
    }
    

    /**
     * Initializes the window for better performance
     * 
     */
    public EditImage(ScreenCapture screenCapture) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                editorPanel = new EditorPanel(screenCapture);
                screenCapture.setSize((int) (screenCapture.getStartingDimension().getWidth() + 500), 500);
                screenCapture.add(editorPanel);
            }
        });
    }
    
    /*
     * Gets the EditorPanel instance of this editor
     */
    public EditorPanel getEditorPanel() {
        return editorPanel;
    }

}