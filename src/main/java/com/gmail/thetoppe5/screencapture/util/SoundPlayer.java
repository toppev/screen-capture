package com.gmail.thetoppe5.screencapture.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.gmail.thetoppe5.screencapture.ScreenCapture;

public final class SoundPlayer {

    private SoundPlayer() {
    }

    /**
     * Plays InputStream as sound
     * 
     * @param input
     *            input stream to play
     * @return true if started playing successfully
     */
    public static boolean playSound(InputStream input) {
        // make sure the input supports mark/reset
        InputStream bufferedIn = new BufferedInputStream(input);

        AudioInputStream audioInputStream;
        try {
            // read the audio
            audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            // open and start playing
            clip.open(audioInputStream);
            clip.start();
            return true;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            ScreenCapture.getLogger().log(Level.SEVERE, "Failed to play audio", e);
        }
        return false;
    }

    /**
     * Plays File as sound. Supports .wav
     * 
     * @param filo
     *            to play
     * @return true if started playing successfully
     */
    public static boolean playSound(File file) {
        if (file == null) {
            ScreenCapture.getLogger().log(Level.WARNING, "Failed to play audio: file is null");
            return false;
        }
        if (!file.exists()) {
            ScreenCapture.getLogger().log(Level.WARNING, "Failed to play audio: file not found {0}", file.getPath());
            return false;
        }
        InputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            ScreenCapture.getLogger().log(Level.SEVERE, "File not found", e);
        }
        return playSound(input);
    }

    /**
     * Plays the camera_click.wav
     * 
     * @return true if starts playing successfully, otherwise false
     */
    public static boolean playScreenshotSound() {
        InputStream stream = ResourceUtil.getResource("camera_click.wav");
        return playSound(stream);
    }
}
