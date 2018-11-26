package com.gmail.thetoppe5.screencapture.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.gmail.thetoppe5.screencapture.ScreenCapture;

public class EditorPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final ScreenCapture parent;

    private final BufferedImage image;
    private final BufferedImage backupImage;

    private EditMode editMode = EditMode.FREE;
    private Point clicked;
    private int size = 10;
    private Color color = Color.RED;
    private JColorChooser colorChooser = new JColorChooser(color);
    private JFrame colorChooserFrame = new JFrame();

    public EditorPanel(BufferedImage image, ScreenCapture parent) {
        this.setLayout(new GridLayout());
        this.image = image;
        this.parent = parent;
        this.backupImage = deepCopy(this.image);
        // weird stuff
        // this.setBorder(LineBorder.createBlackLineBorder());
        this.setSize(500, 500);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        colorChooserFrame.setSize(colorChooser.getPreferredSize());
        colorChooserFrame.add(colorChooser);
        addScrollPane();
        repaint();
        updateTitle();
    }

    private void addScrollPane() {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                JScrollPane scroll = new JScrollPane(EditorPanel.this);
                scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                scroll.setVisible(true);
                parent.add(scroll);
            }
        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, (int) parent.getStartingDimension().getWidth(), 0, image.getWidth(), image.getHeight(),
                this);
        parent.repaint();
    }

    public void mouse(int x, int y) {
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (editMode != null) {
            x -= parent.getStartingDimension().getWidth();
            if (editMode == EditMode.FREE) {
                Point p = new Point(x, y);
                g.drawLine(clicked != null ? clicked.x : p.x, clicked != null ? clicked.y : p.y, x, y);
                clicked = p;
            }

            else if (editMode == EditMode.ERASE) {
                g.setPaintMode();
                x -= size / 2;
                y -= size / 2;
                int w = size;
                int h = size;
                if (x < 0) {
                    w += x;
                    x = 0;
                } else if (x + size > image.getWidth()) {
                    w = image.getWidth() - x;
                    x = image.getWidth() - w;
                }
                if (y < 0) {
                    h += y;
                    y = 0;
                } else if (y + size > image.getHeight()) {
                    h = image.getHeight() - y;
                    y = image.getHeight() - h;
                }
                if (w < 1 || w >= image.getWidth() || h < 1 || h >= image.getHeight()) {
                    return;
                }
                BufferedImage subImage = backupImage.getSubimage(x, y, w, h);
                g.drawImage(subImage, x, y, w, h, this);

            }
            this.repaint();
        }
    }

    private void updateTitle() {
        if (editMode == EditMode.FREE) {
            parent.setTitle("Free draw - Size: " + size + " - Color: ");
            parent.setBackground(color);
        } else if (editMode == EditMode.ERASE) {
            parent.setTitle("Eraser - Size: " + size + " - Color: ");
            parent.setBackground(color);
        }
    }

    private void setCurrentSize(int size) {
        this.size = size;
        updateTitle();
    }

    private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        editMode = e.getButton() == 3 ? EditMode.ERASE : EditMode.FREE;
        color = colorChooser.getColor();
        updateTitle();
        if (e.getButton() != 2) {
            mouse(e.getX(), e.getY());
        } else {
            colorChooserFrame.setLocation(this.getLocation());
            colorChooserFrame.setVisible(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        clicked = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getButton() != 2) {
            mouse(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int newSize = this.size + e.getWheelRotation();
        if (newSize < 1)
            newSize = 1;
        if (newSize > getWidth() || newSize > getHeight())
            newSize--;
        setCurrentSize(newSize);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

}