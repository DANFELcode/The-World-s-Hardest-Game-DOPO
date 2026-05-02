package domain;

/**
 * Represents an enemy in the game. Kills any player it contacts. <br>
 * <b>(x, y, width, height, speed, movement)</b> <br>
 * <b>Inv:</b> speed > 0 and movement != null
 */
public class Enemy extends MovableElement implements Lethal {

    private MovementStrategy movement;

    /**
     * Creates an enemy with a given position, size, speed and movement strategy.
     * @param x initial horizontal position
     * @param y initial vertical position
     * @param width enemy width, must be greater than 0
     * @param height enemy height, must be greater than 0
     * @param speed movement speed, must be greater than 0
     * @param movement strategy that defines how the enemy moves
     */
    public Enemy(double x, double y, double width, double height, double speed, MovementStrategy movement) {
        super(x, y, width, height, speed);
        this.movement = movement;
    }

    /**
     * Kills the player on contact.
     * @param player the player that made contact with the enemy
     */
    public void onDestroy(Player player) {
        player.die();
    }
    
    /**
     * Moves the enemy according to its movement strategy.
     * @param map the current level map
     */
    public void move(GameMap map) {
        movement.move(this, map);
    }
}
