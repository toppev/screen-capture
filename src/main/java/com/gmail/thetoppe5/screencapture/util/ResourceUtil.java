package com.gmail.thetoppe5.screencapture.util;

import java.io.InputStream;

public class ResourceUtil {

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
        System.out.println("Resource not found: " + path);
        return null;
    }

}
