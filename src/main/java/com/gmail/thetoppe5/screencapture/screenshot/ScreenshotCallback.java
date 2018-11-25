package com.gmail.thetoppe5.screencapture.screenshot;

import java.awt.image.BufferedImage;

public abstract class ScreenshotCallback {

    public abstract void onFailure(Exception e);

    public abstract void onSuccess(BufferedImage image);

}
