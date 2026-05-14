package domain;

import java.io.Serializable;

/**
 * Defines the movement behavior of an enemy. <br>
 */
public interface MovementStrategy extends Serializable {
	public static final double ENEMY_UNIT = 1.3;

    /**
     * Moves the enemy according to the strategy (one tick worth of movement).
     * @param enemy the enemy to move
     * @param level the current level
     */
    void move(Enemy enemy, Level level);

    /** Returns the movement-specific params used in level files (e.g. "movement=basic,direction=VERTICAL,sign=1"). */
    String toFileParams();
}
