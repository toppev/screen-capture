package com.gmail.thetoppe5.screencapture.util;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer  {


    public static boolean playSound(File file) {
        if(file == null) {
            System.out.println("Couldn't play sound. File is null");
            return false;
        }
        if(!file.exists()) {
            System.out.println("Couldn't play sound. File not found: " + file.getAbsolutePath());
            return false;
        }
        AudioInputStream audioInputStream;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            return true;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean playScreenshotSound() {
        File file = new File("src/main/resources/camera_click.wav");
        return playSound(file);
    }
}
