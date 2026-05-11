package domain;

import java.awt.Color;

/**
 * Represents a player controlled by the machine, using a GameStrategy to decide moves. <br>
 * <b>(name, x, y, width, height, speed, strategy)</b> <br>
 * <b>Inv:</b> strategy != null
 */
public class Machine extends Player {

    private GameStrategy strategy;

    /**
     * Creates a machine player with a strategy.
     * @param name machine name
     * @param x initial horizontal position
     * @param y initial vertical position
     * @param width machine width, must be greater than 0
     * @param height machine height, must be greater than 0
     * @param speed movement speed, must be greater than 0
     * @param strategy the strategy used to decide moves, must not be null
     */
    public Machine(String name, double x, double y, double width, double height, double speed, GameStrategy strategy) {
        super(name, x, y, width, height, speed);
        this.strategy = strategy;
    }

    /**
     * Changes the machine's skin (no effect by default).
     * @param newSkin the color of the new skin
     */
    @Override
    public void changeSkin(String newSkin) {}

    @Override
    public Color getDisplayColor() { return Color.MAGENTA; }
}