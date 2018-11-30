package com.gmail.thetoppe5.screencapture.util;

import java.io.InputStream;

public class ResourceUtil {

    
    
    public static InputStream getResource(String path) {
        InputStream resource = ResourceUtil.class.getClassLoader().getResourceAsStream(path);
        if(resource != null) {
            return resource;
        }
        System.out.println("Resource not found: " + path);
        return null;
    }
    
}
