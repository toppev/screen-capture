package dev.toppe.img.screencapture.uploader;

import dev.toppe.img.screencapture.util.ImageEncoder;
import dev.toppe.img.screencapture.ScreenCapture;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.logging.Level;

/**
 * Implementation to upload to Imgur
 */
public class ToppeDevUploader implements Uploader {

    private static final String BASE_URL = "localhost:8080";
    private static final String WEBSITE_URL = BASE_URL + "/api/upload";
    private String token = "none";

    @Override
    public UploadLink upload(BufferedImage image) {
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
            UploadEntry entry = new UploadEntry("30D", getToken(), imageFile);
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
            String id =  json.get("id").getAsString();
            String url = BASE_URL + "/" + id;
            String imageUrl = BASE_URL + "/img/" + id + ".png";
            // delete the file
            Files.delete(imageFile.toPath());
            ScreenCapture.getLogger().log(Level.INFO, "Uploading took {0} ms\n. URL: {1}", new Object[]{System.currentTimeMillis() - started, url});
            return new UploadLink(imageUrl, url);
        } catch (IOException e) {
            ScreenCapture.getLogger().log(Level.SEVERE, "Failed to get response from uploaded image", e);
        }
        return null;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    private class UploadEntry {

        private String expiration;
        private String image;
        private String token;

        public UploadEntry(String expiration, String token, File image) throws IOException {
            this.expiration = expiration;
            this.token = token;
            this.image = ImageEncoder.toBase64(image);
        }
    }
}
