package com.gmail.thetoppe5.screencapture.uploader;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.apache.xerces.impl.dv.util.Base64;

import com.gmail.thetoppe5.screencapture.ScreenCapture;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Implementation to upload to Imgur
 *
 */
public class ImgurUploader implements AbstractUploader {

    private static final String WEBSITE_URL = "https://api.imgur.com/3/upload";
    private static final String CLIENT_ID = "c614c9715157d42";

    @Override
    public String upload(BufferedImage image) {
        long started = System.currentTimeMillis();

        File imageFile = new File("clipboard.png");
        try {
            ImageIO.write(image, "png", imageFile);
        } catch (IOException e) {
            ScreenCapture.getLogger().log(Level.SEVERE, "Failed to write image file", e);
        }

        // connect
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(WEBSITE_URL).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Client-ID " + CLIENT_ID);
            conn.setReadTimeout(1000 * 20);
            conn.connect();
        } catch (IOException e) {
            ScreenCapture.getLogger().log(Level.SEVERE, "Failed to upload image", e);
        }

        // write
        try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
            writer.write("image=" + toBase64(imageFile));
            writer.flush();
        } catch (IOException e) {
            ScreenCapture.getLogger().log(Level.SEVERE, "Failed to write image OutputStream", e);
        }

        // get response
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JsonParser parser = new JsonParser();
            JsonObject json = (JsonObject) parser.parse(reader);
            JsonObject data = json.get("data").getAsJsonObject();
            String url = "http://i.imgur.com/" + data.get("id").getAsString() + ".png";

            // delete the file
            Files.delete(imageFile.toPath());
            ScreenCapture.getLogger().log(Level.INFO, "Uploading took {0} ms\n. URL: {1}",
                    new Object[] { System.currentTimeMillis() - started, url });
            return url;
        } catch (IOException e) {
            ScreenCapture.getLogger().log(Level.SEVERE, "Failed to get response from uploaded image", e);
        }
        return null;
    }

    private static String toBase64(File file) {
        try (FileInputStream fs = new FileInputStream(file)) {
            byte[] b = new byte[(int) file.length()];
            fs.read(b);
            return URLEncoder.encode(Base64.encode(b), "UTF-8");
        } catch (Exception e) {
            ScreenCapture.getLogger().log(Level.SEVERE, "Failed to convert to base64", e);
            return null;
        }
    }

}
