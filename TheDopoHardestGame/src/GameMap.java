package domain;

/**
 * Represents the game map. Defines boundaries and wall positions. <br>
 * <b>(width, height)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
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

    /**
     * Returns whether the given position and size fit within the map bounds without hitting a wall.
     * @param x horizontal position
     * @param y vertical position
     * @param width element width
     * @param height element height
     * @return true if the position is walkable
     */
    public static boolean isWalkable(double x, double y, double width, double height) {
        return false;
    }

    /**
     * Returns whether the given position contains a wall.
     * @param x horizontal position
     * @param y vertical position
     * @return true if there is a wall at the position
     */
    public boolean isWall(double x, double y) {
        return false;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
