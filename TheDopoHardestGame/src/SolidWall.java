package domain;

/**
 * Represents a solid wall in the game. Blocks movement of all entities. 
 * (x, y, width, height, color)
 * Inv: width > 0 and height > 0
 */
public class SolidWall extends StaticElement {

    /**
     * Creates a solid wall at the given position and size.
     * @param x horizontal position
     * @param y vertical position
     * @param width wall width, must be greater than 0
     * @param height wall height, must be greater than 0
     * @param color visual color of the wall
     */
    public SolidWall(double x, double y, double width, double height, String color) {
        super(x, y, width, height, color);
    }

    /**
     * Returns true since solid walls always block movement.
     * @return true
     */
    @Override
    public boolean isBlocking() {
        return true;
    }
}
