package dev.toppe.img.screencapture.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public final class ImageUtil {

    private ImageUtil() {
    }

    /**
     * Create a new one color image
     * 
     * @param width
     *            the width of the image
     * @param height
     *            the height of the image
     * @param color
     *            the color of the image
     * @return a new width*height sized BufferedImage with background of the given
     *         color
     */
    public static BufferedImage blankImage(int width, int height, Color color) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setBackground(color);
        g.clearRect(0, 0, width, height);
        return bi;
    }

    public static BufferedImage blankImage(int width, int height) {
        return blankImage(width, height, Color.WHITE);
    }

}
