package domain;

import java.io.Serializable;

/**
 * Defines the movement behavior of an enemy. <br>
 */
public interface MovementStrategy extends Serializable {

    /**
     * Moves the enemy according to the strategy.
     * @param enemy the enemy to move
     * @param level the current level
     * @param deltaTime seconds elapsed since the last tick
     */
    void move(Enemy enemy, Level level, double deltaTime);

    /** Returns the movement-specific params used in level files (e.g. "movement=basic,direction=VERTICAL,sign=1"). */
    String toFileParams();
}
