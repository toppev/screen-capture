package com.gmail.thetoppe5.screencapture.editor;

import com.gmail.thetoppe5.screencapture.ScreenCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class EditorPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    private static final long serialVersionUID = -4754990559531832558L;

    private final ScreenCapture screenCapture;
    BufferedImage backupImage;
    // image stuff
    private BufferedImage image;
    // stuff concerning painting (modes, size, last clicked point)
    private EditMode editMode = EditMode.FREE;
    private int size = 10;
    private Point clicked;

    // color stuff
    private Color color = Color.RED;
    private JColorChooser colorChooser = new JColorChooser(color);
    private JFrame colorChooserFrame = new JFrame();
    private long lastClickedTime;

    /**
     * Create a new EditorPanel
     *
     * @param image         the image to edit
     * @param screenCapture instance of the ScreenCapture
     */
    public EditorPanel(BufferedImage image, ScreenCapture screenCapture) {
        this.screenCapture = screenCapture;
        this.setSize(image.getWidth(), image.getHeight());
        this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        colorChooserFrame.setSize(colorChooser.getPreferredSize());
        colorChooserFrame.add(colorChooser);
        addScrollPane();
        updateImage(image);
    }

    /**
     * Copies the image
     *
     * @param bi BufferedImage to copy
     *
     * @return a new copy of the image
     */
    private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    /**
     * Update this EditorPanel's image that is being edited
     *
     * @param newImage the new image
     */
    public void updateImage(BufferedImage newImage) {
        if (newImage != null) {
            this.image = newImage;
            this.backupImage = deepCopy(image);
        }
        for (Component c : this.getComponents()) {
            if (c instanceof EditorTextField) {
                this.remove(c);
            }
        }
        screenCapture.repaint();
        updateTitle();
    }

    /**
     * Adds the JScrollPane as needed Makes it possible to edit higher resolution
     * images without full screen
     */
    private void addScrollPane() {
        SwingUtilities.invokeLater(() -> {
            JScrollPane scroll = new JScrollPane(EditorPanel.this);
            scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scroll.setVisible(true);
            screenCapture.add(scroll);
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // draw the image if it's not null
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
    }

    // not private because EditorTextField needs to access this
    Graphics2D createGraphics() {
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g;
    }

    /**
     * Handle mouse events
     *
     * @param x the x of the mouse event
     * @param y the y of the mouse event
     */
    private void mouse(int x, int y) {
        Graphics2D g = createGraphics();
        if (editMode != null) {
            if (editMode == EditMode.FREE) {
                // handle free drawing
                Point p = new Point(x, y);
                // draws line between because the event is not called enough often to just draw
                // the current fixels
                g.drawLine(clicked != null ? clicked.x : p.x, clicked != null ? clicked.y : p.y, x, y);
                // store the last clicked Point
                clicked = p;
            } else if (editMode == EditMode.ERASE) {
                // handle erasing paintings
                g.setPaintMode();
                x -= size / 2;
                y -= size / 2;
                int w = size;
                int h = size;
                if (x < 0) {
                    // if x is negative expand the eraser in the other direction
                    w += x;
                    x = 0;
                } else if (x + size > image.getWidth()) {
                    // reduce the eraser size if it goes beyond the image
                    w = image.getWidth() - x;
                    x = image.getWidth() - w;
                }
                if (y < 0) {
                    // if y is negative expand the eraser in the other direction
                    h += y;
                    y = 0;
                } else if (y + size > image.getHeight()) {
                    // reduce the eraser size if it goes beyond the image
                    h = image.getHeight() - y;
                    y = image.getHeight() - h;
                }
                // ignore width and height if outside the image borders
                if (w < 1 || w >= image.getWidth() || h < 1 || h >= image.getHeight()) {
                    return;
                }
                // select the area from the backupImage (the image without any paintings)
                BufferedImage subImage = backupImage.getSubimage(x, y, w, h);
                // and draw it in the current image
                g.drawImage(subImage, x, y, w, h, this);
            }
            screenCapture.repaint();
        }
    }

    /**
     * Updates the title of this frame
     */
    private void updateTitle() {
        if (editMode == EditMode.FREE) {
            screenCapture.setTitle("Free draw - Size: " + size + " - Color: ");
            screenCapture.setBackground(color);
        } else if (editMode == EditMode.ERASE) {
            screenCapture.setTitle("Eraser - Size: " + size + " - Color: ");
            screenCapture.setBackground(color);
        }
    }

    /**
     * Sets the size of the painting tool/eraser
     *
     * @param size
     */
    private void setCurrentSize(int size) {
        this.size = size;
        // update the title with the new size
        updateTitle();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getButton() != 2) {
            mouse(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // No need to do anything
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int newSize = this.size + e.getWheelRotation();
        if (newSize < 1) {
            newSize = 1;
        }
        if (newSize > getWidth() || newSize > getHeight()) {
            newSize--;
        }
        setCurrentSize(newSize);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // No need to do anything
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // double click to type text
        if (lastClickedTime + 300 > System.currentTimeMillis()) {
            // avoid bugs if clicked 3 times in row
            lastClickedTime = 0;
            editMode = EditMode.TEXT;
            clicked = e.getPoint();
            EditorTextField textField = new EditorTextField(this);
            textField.setFont(new Font("Verdana", Font.BOLD, size * 2));
            textField.setForeground(color);
            textField.setLocation(clicked);
            this.add(textField);
            textField.requestFocusInWindow();

        } else {
            this.requestFocusInWindow();
            lastClickedTime = System.currentTimeMillis();
            if (editMode == EditMode.TEXT) {
                editMode = EditMode.FREE;
            }
            editMode = e.getButton() == 3 ? EditMode.ERASE : EditMode.FREE;
            color = colorChooser.getColor();
            updateTitle();
            // Open color chooser with middle-click
            if (e.getButton() == 2) {
                colorChooserFrame.setLocation(this.getLocation());
                colorChooserFrame.setVisible(true);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        clicked = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // No need to do anything
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // No need to do anything
    }

    /**
     * Get the current image
     *
     * @return the current image
     */
    public BufferedImage getImage() {
        return image;
    }

}