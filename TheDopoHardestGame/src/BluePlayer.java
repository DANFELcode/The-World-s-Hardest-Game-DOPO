package domain;

import java.awt.Color;

/**
 * Represents the fast player with increased speed. <br>
 * <b>(name, x, y, width=20.0, height=20.0, speed=1.5)</b> <br>
 * <b>Inv:</b> speed > 0 and width > 0 and height > 0
 */
public class BluePlayer extends Player {

    /**
     * Creates a blue player with default size (20x20) and speed (1.5).
     * @param name player name
     * @param x initial horizontal position
     * @param y initial vertical position
     */
    public BluePlayer(String name, double x, double y) {
        super(name, x, y, 20.0, 20.0, 1.5);
    }

    /**
     * Changes the player's skin (no effect by default).
     * @param newSkin the color of the new skin
     */
    @Override
    public void changeSkin(String newSkin) {}

    @Override
    public Color getDisplayColor() { return Color.BLUE; }
}
