package com.gmail.thetoppe5.screencapture.uploader;

import com.gmail.thetoppe5.screencapture.ScreenCapture;
import com.gmail.thetoppe5.screencapture.util.ImageEncoder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.xerces.impl.dv.util.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.logging.Level;

/**
 * Implementation to upload to Imgur
 */
public class ToppeDevUploader implements Uploader {

    // Will change to https later, or just redirect on the server-side
    private static final String WEBSITE_URL = "http://img.toppe.dev/upload";

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
            conn.setReadTimeout(1000 * 20);
            conn.connect();
        } catch (IOException e) {
            ScreenCapture.getLogger().log(Level.SEVERE, "Failed to upload image", e);
        }

        // write
        try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
            UploadEntry entry = new UploadEntry("30D", imageFile);
            writer.write(new Gson().toJson(entry));
            writer.flush();
        } catch (IOException e) {
            ScreenCapture.getLogger().log(Level.SEVERE, "Failed to write image OutputStream", e);
        }

        // get response
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JsonParser parser = new JsonParser();
            JsonObject json = (JsonObject) parser.parse(reader);
            String url = "http://img.toppe.dev/img/" + json.get("id").getAsString();

            // delete the file
            Files.delete(imageFile.toPath());
            ScreenCapture.getLogger().log(Level.INFO, "Uploading took {0} ms\n. URL: {1}", new Object[]{System.currentTimeMillis() - started, url});
            return url;
        } catch (IOException e) {
            ScreenCapture.getLogger().log(Level.SEVERE, "Failed to get response from uploaded image", e);
        }
        return null;
    }

    private class UploadEntry {

        private String expiration;
        private String image;

        public UploadEntry(String expiration, File image) throws IOException {
            this.expiration = expiration;
            this.image = ImageEncoder.toBase64(image);
        }
    }
}