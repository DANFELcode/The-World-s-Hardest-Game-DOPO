package domain;

/**
 * Defines the movement behavior of an enemy. <br>
 */
public interface MovementStrategy {

    /**
     * Moves the enemy according to the strategy.
     * @param enemy the enemy to move
     * @param level the current level
     */
    void move(Enemy enemy, Level level);
}
