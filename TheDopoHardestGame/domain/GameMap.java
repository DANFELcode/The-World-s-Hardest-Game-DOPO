package domain;

import java.awt.Color;

/**
 * Represents the game map. Defines the boundaries of the level. <br>
 * <b>(width, height, backgroundColor)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public class GameMap {
    private int width;
    private int height;
    private Color backgroundColor;

    /**
     * Creates a map with the given dimensions and default background color.
     * @param width map width, must be greater than 0
     * @param height map height, must be greater than 0
     */
    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.backgroundColor = GameConstants.COLOR_BOARD;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Color getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(Color backgroundColor) { this.backgroundColor = backgroundColor; }
}
