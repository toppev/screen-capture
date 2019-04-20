package com.gmail.thetoppe5.screencapture.uploader;

import java.awt.image.BufferedImage;

public interface AbstractUploader {

    /**
     * Uploads the given image
     * 
     * @param image
     *            BufferedImage to upload
     * @return url of the uploaded image as String
     */
    public String upload(BufferedImage image);

}
