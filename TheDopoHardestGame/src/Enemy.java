package domain;

import java.awt.Color;

/**
 * Represents an enemy in the game. Kills any player it contacts. <br>
 * <b>(x, y, width, height, movement)</b> <br>
 * <b>Inv:</b> movement != null
 */
public class Enemy extends MovableElement implements Lethal {

    private MovementStrategy movement;
    private boolean isDead; // Estado para interacción con bombas

    /**
     * Creates an enemy with a given position, size, and movement strategy.
     * Speed is set to 1.0 by default as per the new design.
     * @param x initial horizontal position
     * @param y initial vertical position
     * @param width enemy width, must be greater than 0
     * @param height enemy height, must be greater than 0
     * @param movement strategy that defines how the enemy moves
     */
    public Enemy(double x, double y, double width, double height, MovementStrategy movement) {
        // Mantenemos el 1.0 de velocidad que puso tu compañero
        super(x, y, width, height, 1.0);
        this.movement = movement;
        this.isDead = false;
    }

    /**
     * Marks the enemy as dead so it can be removed from the level (by bombs).
     */
    public void die() {
        this.isDead = true;
    }

    /**
     * Returns whether the enemy has been destroyed.
     * @return true if dead
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Kills the player on contact.
     * @param player the player that made contact with the enemy
     */
    @Override
    public void onDestroy(Player player) {
        player.die();
    }
    
    /**
     * Moves the enemy according to its movement strategy.
     * @param level the current level
     */
    public void move(Level level) {
        movement.move(this, level);
    }

    /**
     * Returns the visual color of the enemy.
     * @return blueish color
     */
    public Color getDisplayColor() {
        return new Color(40, 60, 200);
    }
}