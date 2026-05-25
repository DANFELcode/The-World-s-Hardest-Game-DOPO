package presentation;

import domain.GameLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import javax.sound.sampled.*;
import javax.swing.Timer;

/**
 * Handles background music: loads WAV tracks, loops them, and exposes
 * volume and mute controls. Configuration is persisted to a properties file.
 * All failures are silently logged so the game always starts even if audio
 * is unavailable.
 */
public class MusicPlayer {

    private static final String[] TRACK_PATHS = {
        "resources/music/EDM-Detection-Mode.wav",
        "resources/music/Voltaic.wav"
    };
    public static final String[] TRACK_NAMES = {
        "EDM Detection Mode",
        "Voltaic"
    };
    private static final String CONFIG_PATH = "resources/music/config.properties";

    private Clip clip;
    private int currentTrack = 0;
    private float volume = 0.5f;   // linear 0.0–1.0
    private boolean muted = false;
    private boolean userStopped = false;
    private Timer watchdog;

    /** Creates the player, restores persisted settings, and starts the configured track. */
    public MusicPlayer() {
        loadConfig();
        loadAndPlay(currentTrack);
        startWatchdog();
    }

    //en windows el clip se cae solo despues de un rato, lo revivimos cada 2s
    private void startWatchdog() {
        watchdog = new Timer(2000, e -> {
            if (userStopped) return;
            if (clip == null || !clip.isOpen() || !clip.isRunning()) {
                stopClip();
                loadAndPlay(currentTrack);
            }
        });
        watchdog.start();
    }

    /** Switches to the given track index (0-based). No-op if already playing it. */
    public void setTrack(int index) {
        if (index < 0 || index >= TRACK_PATHS.length) return;
        if (index == currentTrack && clip != null && clip.isRunning()) return;
        stopClip();
        currentTrack = index;
        loadAndPlay(currentTrack);
    }

    /** Sets volume in [0.0, 1.0]. Clamped silently. */
    public void setVolume(float v) {
        volume = Math.max(0f, Math.min(1f, v));
        applyVolume();
    }

    /** Mutes or unmutes without changing the stored volume level. */
    public void setMuted(boolean muted) {
        this.muted = muted;
        applyVolume();
    }

    /** Stops playback and releases the audio resource. */
    public void stop() {
        userStopped = true;
        if (watchdog != null) watchdog.stop();
        stopClip();
    }

    public int getCurrentTrack() { return currentTrack; }
    public float getVolume()     { return volume; }
    public boolean isMuted()     { return muted; }

    /** Persists current track, volume and mute state to the config file. */
    public void saveConfig() {
        try {
            Properties props = new Properties();
            props.setProperty("track",  String.valueOf(currentTrack));
            props.setProperty("volume", String.valueOf(volume));
            props.setProperty("muted",  String.valueOf(muted));
            try (FileOutputStream fos = new FileOutputStream(CONFIG_PATH)) {
                props.store(fos, "Music configuration");
            }
        } catch (Exception e) {
            GameLogger.getInstance().logError("Failed to save music config", e);
        }
    }

 

    private void loadConfig() {
        try {
            File f = new File(CONFIG_PATH);
            if (!f.exists()) return;
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(f)) {
                props.load(fis);
            }
            currentTrack = Integer.parseInt(props.getProperty("track",  "0"));
            volume       = Float.parseFloat( props.getProperty("volume", "0.8"));
            muted        = Boolean.parseBoolean(props.getProperty("muted", "false"));
            currentTrack = Math.max(0, Math.min(TRACK_PATHS.length - 1, currentTrack));
            volume       = Math.max(0f, Math.min(1f, volume));
        } catch (Exception e) {
            GameLogger.getInstance().logError("Failed to load music config", e);
        }
    }

    private void loadAndPlay(int index) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(TRACK_PATHS[index]));
            clip = AudioSystem.getClip();
            clip.open(ais);
            applyVolume();
            //refuerzo del watchdog: si el clip se detiene reiniciamos el loop
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP
                        && clip != null && clip.isOpen()) {
                    clip.setFramePosition(0);
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                }
            });
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            clip = null;
            GameLogger.getInstance().logError("Failed to load music: " + TRACK_PATHS[index], e);
        }
    }

    private void stopClip() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }

    private void applyVolume() {
        if (clip == null) return;
        try {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = muted ? gain.getMinimum() : linearToDb(volume, gain.getMinimum());
            gain.setValue(dB);
        } catch (Exception ignored) {
            // FloatControl not supported on this audio system — skip silently.
        }
    }

    /** Converts a linear gain in [0,1] to decibels, clamped to the clip's minimum. */
    private static float linearToDb(float linear, float minDb) {
        if (linear <= 0f) return minDb;
        return Math.max(minDb, (float)(20.0 * Math.log10(linear)));
    }
}
