package com.gmail.thetoppe5.screencapture.util;

import com.gmail.thetoppe5.screencapture.ScreenCapture;
import org.apache.xerces.impl.dv.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public final class ImageEncoder {

    private ImageEncoder() {
    }

    public static String toBase64(File file) {
        try (FileInputStream fs = new FileInputStream(file)) {
            byte[] b = new byte[(int) file.length()];
            fs.read(b);
            return URLEncoder.encode(Base64.encode(b), StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            ScreenCapture.getLogger().log(Level.SEVERE, "Failed to convert to base64", e);
            return null;
        }
    }

}
