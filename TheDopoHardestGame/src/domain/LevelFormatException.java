package domain;

/** Thrown when a level file is malformed (missing required fields, bad parameters). */
public class LevelFormatException extends GameException {

    private final String filePath;
    private final String reason;

    public LevelFormatException(String filePath, String reason) {
        super("Formato inválido en " + filePath + ": " + reason);
        this.filePath = filePath;
        this.reason = reason;
        GameLogger.getInstance().logError("[level-format:" + filePath + "] " + reason, this);
    }

    public String getFilePath() { return filePath; }
    public String getReason() { return reason; }
}
