package domain;

import java.io.Serializable;

/**
 * Defines the behavior strategy of the machine player. <br>
 */
public interface GameStrategy extends Serializable {

    /**
     * Executes the strategy for the machine player.
     * @param machine the machine player
     * @param level the current level
     */
    void execute(Machine machine, Level level);
}
