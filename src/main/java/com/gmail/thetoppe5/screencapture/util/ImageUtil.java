package com.gmail.thetoppe5.screencapture.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageUtil {

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
