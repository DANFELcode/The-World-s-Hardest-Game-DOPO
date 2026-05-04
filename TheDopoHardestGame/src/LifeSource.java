package domain;

/**
 * Represents a static collectible element that updates the player's spawn point on contact. <br>
 * <b>(x, y, width, height, color)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public class LifeSource extends StaticElement implements Collectible {

    /**
     * Creates a life source at the given position and size.
     * @param x horizontal position
     * @param y vertical position
     * @param width life source width, must be greater than 0
     * @param height life source height, must be greater than 0
     * @param color life source color
     */
    public LifeSource(double x, double y, double width, double height, String color) {
        super(x, y, width, height, color);
    }

    /**
     * Updates the player's spawn point to this element's position.
     * @param player the player that collected this life source
     */
    @Override
    public void onCollect(Player player) {}
}
