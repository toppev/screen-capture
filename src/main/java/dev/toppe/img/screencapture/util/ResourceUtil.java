package dev.toppe.img.screencapture.util;

import java.io.InputStream;
import java.util.logging.Level;

import dev.toppe.img.screencapture.ScreenCapture;

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
