package domain;

/**
 * Defines the movement behavior of an enemy. <br>
 */
public interface MovementStrategy {

    /**
     * Moves the enemy according to the strategy.
     * @param enemy the enemy to move
     * @param map the current level map
     */
    void move(Enemy enemy, GameMap map);
}
