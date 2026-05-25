package domain;
import java.util.logging.*;
import java.util.logging.Level;
import java.io.IOException;

/** Singleton that records game errors to errors.log for developers. */
public final class GameLogger {
    private static GameLogger instance;
    private static final Logger LOGGER = Logger.getLogger("DOPOHardestGame");

    private GameLogger() {
        try {
            FileHandler handler = new FileHandler("errors.log", true);
            handler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(handler);
            LOGGER.setLevel(Level.SEVERE);
            LOGGER.setUseParentHandlers(false);
        } catch (IOException e) {
            // If the log file can't be created, fall back to default handlers silently.
        }
    }

    public static GameLogger getInstance() {
        if (instance == null) instance = new GameLogger();
        return instance;
    }

    /** Records a game error with its causing exception. */
    public void logError(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message, e);
    }
}
