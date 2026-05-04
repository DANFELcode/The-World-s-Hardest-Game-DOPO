package domain;

/**
 * Represents the game map. Defines the boundaries of the level.
 * (width, height)
 * Inv: width > 0 and height > 0
 */
public class GameMap {
    private int width;
    private int height;

    /**
     * Creates a map with the given dimensions.
     * @param width map width, must be greater than 0
     * @param height map height, must be greater than 0
     */
    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
