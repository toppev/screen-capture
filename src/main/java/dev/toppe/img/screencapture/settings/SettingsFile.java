package dev.toppe.img.screencapture.settings;

import dev.toppe.img.screencapture.ScreenCapture;
import dev.toppe.img.screencapture.uploader.Uploader;
import dev.toppe.img.screencapture.uploader.UploaderProvider;

import java.io.*;
import java.util.Properties;

/**
 * Loads and saves settings but does not hold the settings. All saving and loading should be done here.
 */
public class SettingsFile {

    private File settingsFile;


    public File getSettingsFile() {
        if(settingsFile == null){
            settingsFile = new File(System.getProperty("user.home"), "screen_capture.properties");
        }
        return settingsFile;
    }

    public void save() {
        try{
            Properties props = new Properties();
            for (Uploader uploader : UploaderProvider.getUploaders()) {
                props.setProperty(uploader.getClass().getName() + ".token", uploader.getToken());
            }
            if(!settingsFile.exists()) {
                ScreenCapture.getLogger().info("Settings file does not exist. Creating one at " + getSettingsFile().getPath());
            }
            props.store(new FileWriter(getSettingsFile()), "screen-capture properties");
            ScreenCapture.getLogger().info("Settings saved to " + getSettingsFile().getPath());
        }catch(IOException e) {
            ScreenCapture.getLogger().warning("Failed to load settings" + e);
        }
    }

    public void load() {
        try{
            File file = getSettingsFile();
            if(!file.exists()) {
                ScreenCapture.getLogger().info("Settings file does not exist at " + getSettingsFile().getPath());
            }
            else {
                Properties props = new Properties();
                props.load(new FileReader(file));
                for (Uploader uploader : UploaderProvider.getUploaders()) {
                    String token = props.getProperty(uploader.getClass().getName() + ".token");
                    if (token != null) {
                        uploader.setToken(token);
                    }
                }
                ScreenCapture.getLogger().info("Settings loaded from " + getSettingsFile().getPath());
            }
        }catch(IOException e) {
            ScreenCapture.getLogger().warning("Failed to load settings" + e);
        }
    }
}
