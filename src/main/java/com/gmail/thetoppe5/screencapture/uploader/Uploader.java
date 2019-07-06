package com.gmail.thetoppe5.screencapture.uploader;

import java.awt.image.BufferedImage;

/**
 * Uploader interface. Contains only one method {@link #upload(BufferedImage)}
 *
 */
public interface Uploader {

    /**
     * Uploads the given image
     * 
     * @param image
     *            BufferedImage to upload
     * @return url of the uploaded image as String
     */
    String upload(BufferedImage image);

}
