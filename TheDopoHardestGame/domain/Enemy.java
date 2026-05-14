package domain;

import java.awt.Color;

/**
 * Represents an enemy in the game. Kills any player it contacts. <br>
 * <b>(x, y, width, height, movement)</b> <br>
 * <b>Inv:</b> movement != null
 */
public class Enemy extends MovableElement implements Lethal, Interactable, Drawable {

    private MovementStrategy movement;
    private boolean isDead;

    public Enemy(double x, double y, double width, double height, MovementStrategy movement) {
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

    public boolean isDead() {
        return isDead;
    }

    @Override
    public void onDestroy(Player player) {
        player.onHit();
    }

    @Override
    public void onPlayerContact(Player player, Level level) {
        if (!isDead) {
            onDestroy(player);
            level.onPlayerDeath(player);
        }
    }

    @Override
    public boolean shouldRemove() { return isDead; }

    public void move(Level level) {
        movement.move(this, level);
    }

    public MovementStrategy getMovement() { return movement; }

    public Color getDisplayColor() {
        return new Color(40, 60, 200);
    }

    @Override
    public DrawCommand toDrawCommand() {
        return new DrawCommand(getDisplayColor(), (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight(), DrawCommand.Shape.OVAL);
    }
}
