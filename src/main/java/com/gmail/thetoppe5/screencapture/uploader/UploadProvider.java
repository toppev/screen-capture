package com.gmail.thetoppe5.screencapture.uploader;

public class UploadProvider {

    public static IUploader getProvider() {
        return new ImgurUploader();
    }

}
