package com.gmail.thetoppe5.screencapture.uploader;

public class UploadProvider {

    private static IUploader uploader;
    private static ImgurUploader defaultProvider = new ImgurUploader();

    /**
     * Get the current main upload provider as IUploader
     * 
     * @return the current upload provider
     */
    public static IUploader getProvider() {
        return uploader == null ? defaultProvider : uploader;
    }

    public static void setProvider(IUploader uploader) {
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
