package domain;

/**
 * Represents a patrol movement strategy. The enemy follows a fixed route between waypoints. <br>
 * <b>()</b> <br>
 * <b>Inv:</b> true
 */
public class PatrolMovement implements MovementStrategy {

    /**
     * Moves the enemy along its patrol route.
     * @param enemy the enemy to move
     * @param level the current level
     */
    @Override
    public void move(Enemy enemy, Level level) {}
}
