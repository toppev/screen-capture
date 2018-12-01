package com.gmail.thetoppe5.screencapture.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Transferable implementation to allow copying BufferedImage to clipboard
 */
public class TransferableImage implements Transferable {

    private final BufferedImage image;

    /**
     * Create a new TransferableImage with the given BufferedImage
     * 
     * @param image
     *            the image to make Transferable
     */
    public TransferableImage(final BufferedImage image) {
        this.image = image;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { DataFlavor.imageFlavor };
    }

    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return DataFlavor.imageFlavor.equals(flavor);
    }

    @Override
    public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return image;
        }
        throw new UnsupportedFlavorException(flavor);
    }
};
