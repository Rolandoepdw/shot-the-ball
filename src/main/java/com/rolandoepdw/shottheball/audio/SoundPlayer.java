package com.rolandoepdw.shottheball.audio;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

/** Plays WAV effects loaded from the classpath, without busy-wait threads. */
public final class SoundPlayer {

    private static final Logger LOG = Logger.getLogger(SoundPlayer.class.getName());
    private static final String PREFIX = "sounds/";

    /** Plays an effect; any failure is logged and never interrupts the game. */
    public void play(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        String resource = PREFIX + fileName;
        try (InputStream in = SoundPlayer.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                LOG.warning("Sound not found: " + resource);
                return;
            }
            try (AudioInputStream audio = AudioSystem.getAudioInputStream(in)) {
                Clip clip = AudioSystem.getClip();
                clip.open(audio);
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
                clip.start();
            }
        } catch (IOException | javax.sound.sampled.LineUnavailableException
                 | javax.sound.sampled.UnsupportedAudioFileException e) {
            LOG.log(Level.WARNING, "Could not play " + resource, e);
        }
    }
}
