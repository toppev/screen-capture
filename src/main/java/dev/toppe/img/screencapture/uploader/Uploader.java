package dev.toppe.img.screencapture.uploader;

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
     * @return UploadLink containing the links
     */
    UploadLink upload(BufferedImage image);

    String getToken();

    void setToken(String token);

}
