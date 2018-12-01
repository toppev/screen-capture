package com.gmail.thetoppe5.screencapture.uploader;

public class UploadProvider {

    /**
     * Get the current main upload provider as IUploader
     * 
     * @return the current upload provider
     */
    public static IUploader getProvider() {
        return new ImgurUploader();
    }

}
