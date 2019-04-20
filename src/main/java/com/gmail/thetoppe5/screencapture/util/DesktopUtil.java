package com.gmail.thetoppe5.screencapture.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import com.gmail.thetoppe5.screencapture.ScreenCapture;

public class DesktopUtil {

    private DesktopUtil() {
    }

    /**
     * Opens the url with default browser
     * 
     * @param url
     *            the url to open
     */
    public static void openInBrowser(String url) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e1) {
                ScreenCapture.getLogger().log(Level.WARNING, "Failed to open browser", e1);
            }
        }
    }

}
