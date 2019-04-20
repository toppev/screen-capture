package com.gmail.thetoppe5.screencapture.screenshot;

import java.awt.image.BufferedImage;

public interface ScreenshotCallback {
    
    public void onFailure(Exception e);

    public void onSuccess(BufferedImage image);

}
