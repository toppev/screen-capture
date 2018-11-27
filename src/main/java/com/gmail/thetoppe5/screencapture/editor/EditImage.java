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
    public EditImage(ScreenCapture window, BufferedImage bufferedImage) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                editorPanel = new EditorPanel(bufferedImage, window);
                window.setSize((int) (window.getStartingDimension().getWidth() + 500), 500);
                // window.setLocationRelativeTo(null);
                // this.setResizable(false);
                // preview.setLocation(x, y);
                window.add(editorPanel);
                // window.pack();
                // window.setVisible(true);
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