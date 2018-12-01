package com.gmail.thetoppe5.screencapture.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class DesktopUtil {

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
                e1.printStackTrace();
            }
        }
    }

}
