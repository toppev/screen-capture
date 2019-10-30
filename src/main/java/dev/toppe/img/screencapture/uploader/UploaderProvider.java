package dev.toppe.img.screencapture.uploader;

import java.sql.SQLOutput;

/**
 * Helper class to manage which {@link Uploader} will be use
 *
 */
public final class UploaderProvider {

    private static Uploader[] uploaders = new Uploader[]{new ToppeDevUploader(), new ImgurUploader()};
    private static Uploader uploader;
    private static Uploader defaultProvider = uploaders[0];

    private UploaderProvider() {
    }

    /**
     * Get the current main upload provider as {@link Uploader}. The default value
     * is {@link ImgurUploader}
     * 
     * @return the current upload provider
     */
    public static Uploader getUploader() {
        return uploader == null ? defaultProvider : uploader;
    }

    /**
     * Set the new {@link Uploader} to use
     * 
     * @param uploader
     *            the new uploader to use, not null
     */
    public static void setUploader(Uploader uploader) {
        if (uploader == null) {
            throw new IllegalArgumentException("uploader must not be null");
        }
        UploaderProvider.uploader = uploader;
    }

    /**
     * Get default provider
     * 
     * @return default upload provider
     */
    public static Uploader getDefaultProvider() {
        return defaultProvider;
    }

    public static Uploader[] getUploaders() {
        return uploaders;
    }

}
