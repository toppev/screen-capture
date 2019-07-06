package com.gmail.thetoppe5.screencapture.util;

import java.io.InputStream;
import java.util.logging.Level;

import com.gmail.thetoppe5.screencapture.ScreenCapture;

public final class ResourceUtil {

    private ResourceUtil() {
    }

    /**
     * Get a resource as stream
     * 
     * @param path
     *            the path, starts from inside the jar
     * @return the resource as InputStream
     */
    public static InputStream getResource(String path) {
        InputStream resource = ResourceUtil.class.getClassLoader().getResourceAsStream(path);
        if (resource != null) {
            return resource;
        }
        ScreenCapture.getLogger().log(Level.SEVERE, "Resource not found: {0}", path);
        return null;
    }

}
