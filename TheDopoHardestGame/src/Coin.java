package domain;

import java.awt.Color;

/**
 * Represents a collectible coin in the game. <br>
 * <b>(x, y, width, height, collected)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public class Coin extends StaticElement implements Collectible {

    private boolean collected;

    /**
     * Creates a coin at the given position and size.
     * @param x horizontal position
     * @param y vertical position
     * @param width coin width, must be greater than 0
     * @param height coin height, must be greater than 0
     */
    public Coin(double x, double y, double width, double height, String color) {
        super(x, y, width, height, color);
        this.collected = false;
    }

    /**
     * Marks the coin as collected.
     * @param player the player that collected the coin
     */
    public void onCollect(Player player) {
        this.collected = true;
    }

    /**
     * Returns whether this coin has been collected.
     * @return true if the coin has been collected
     */
    public boolean isCollected() {
        return collected;
    }

    public void reset() {
        this.collected = false;
    }
    
    public Color getDisplayColor() {
        return new Color(218, 165, 32);   
    }
}