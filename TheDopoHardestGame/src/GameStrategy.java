package domain;

/**
 * Defines the behavior strategy of the machine player. 
 */
public interface GameStrategy {

    /**
     * Executes the strategy for the machine player.
     * @param machine the machine player
     * @param level the current level
     */
    void execute(Machine machine, Level level);
}
