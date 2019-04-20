package com.gmail.thetoppe5.screencapture.uploader;

public class UploadProvider {

    private static AbstractUploader uploader;
    private static ImgurUploader defaultProvider = new ImgurUploader();

    private UploadProvider() {
    }

    /**
     * Get the current main upload provider as IUploader
     * 
     * @return the current upload provider
     */
    public static AbstractUploader getProvider() {
        return uploader == null ? defaultProvider : uploader;
    }

    public static void setProvider(AbstractUploader uploader) {
        UploadProvider.uploader = uploader;
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
