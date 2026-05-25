package domain;

/**
 * Base class for all checked exceptions thrown by the game domain.
 * Subclasses (PersistenceException, LevelIOException, LevelFormatException) auto-log via GameLogger.
 */
public abstract class GameException extends Exception {
	
	public GameException(String message) {
		super(message);
	}
	
	public GameException(String message, Throwable cause) {
	    super(message, cause);
	}
	
	
}
