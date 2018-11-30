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

import javax.imageio.ImageIO;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * Implementation to upload to Imgur
 * 
 * @author Topias
 *
 */
public class ImgurUploader implements IUploader {

    private static final String WEBSITE_URL = "https://api.imgur.com/3/upload";
    private static final String CLIENT_ID = "c614c9715157d42";

    @Override
    public String upload(BufferedImage image) {
        long started = System.currentTimeMillis();

        File imageFile = new File("clipboard.png");
        try {
            ImageIO.write(image, "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }

        // write
        try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
            writer.write("image=" + toBase64(imageFile));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get response
        try {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append(System.lineSeparator());
            }
            reader.close();
            String result = builder.toString();

            JsonParser parser = new JsonParser();
            JsonObject json = (JsonObject) parser.parse(result);
            JsonObject data = json.get("data").getAsJsonObject();
            String url = "http://i.imgur.com/" + data.get("id").getAsString() + ".png";

            // delete the file
            imageFile.delete();

            // System.out.print("Uploading took " + (System.currentTimeMillis()-started) + "
            // ms\n URL: " + url);
            return url;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String toBase64(File file) {
        try {
            byte[] b = new byte[(int) file.length()];
            FileInputStream fs = new FileInputStream(file);
            fs.read(b);
            fs.close();
            return URLEncoder.encode(Base64.encode(b), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
