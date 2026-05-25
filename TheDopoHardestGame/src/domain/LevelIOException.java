package domain;

/** Thrown when reading or writing a level file fails (I/O issue). */
public class LevelIOException extends GameException {

    private final String filePath;

    public LevelIOException(String filePath, String message) {
        super(message);
        this.filePath = filePath;
        GameLogger.getInstance().logError("[level-io:" + filePath + "] " + message, this);
    }

    public String getFilePath() { return filePath; }
}
