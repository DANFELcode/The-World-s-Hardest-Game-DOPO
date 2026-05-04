package domain;

/**
 * Represents a static lethal element that kills the player on contact. 
 * (x, y, width, height, color)
 * Inv: width > 0 and height > 0
 */
public class Bomb extends StaticElement implements Lethal {

    /**
     * Creates a bomb at the given position and size.
     * @param x horizontal position
     * @param y vertical position
     * @param width bomb width, must be greater than 0
     * @param height bomb height, must be greater than 0
     * @param color bomb color
     */
    public Bomb(double x, double y, double width, double height, String color) {
        super(x, y, width, height, color);
    }

    /**
     * Kills the player that touched the bomb.
     * @param player the player that collided with this bomb
     */
    @Override
    public void onDestroy(Player player) {}
}
