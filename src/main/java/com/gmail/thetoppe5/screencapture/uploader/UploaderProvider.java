package com.gmail.thetoppe5.screencapture.uploader;

/**
 * Helper class to manage which {@link Uploader} will be use
 *
 */
public final class UploaderProvider {

    private static Uploader uploader;
    private static ImgurUploader defaultProvider = new ImgurUploader();

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
     * @return default upload provider, ImgurUploader
     */
    public static ImgurUploader getDefaultProvider() {
        return defaultProvider;
    }

}
