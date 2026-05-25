package domain;

/** Thrown when saving or opening a game state fails. */
public class PersistenceException extends GameException {

    private final String operation;

    public PersistenceException(String operation, String message) {
        super(message);
        this.operation = operation;
        GameLogger.getInstance().logError("[persistence:" + operation + "] " + message, this);
    }

    public String getOperation() { return operation; }
}
