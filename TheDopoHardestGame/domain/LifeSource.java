package domain;

import java.awt.Color;

/**
 * Represents a static collectible element that gives an extra life to the
 * player on contact. <br>
 * <b>(x, y, width, height, color)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public class LifeSource extends StaticElement implements Collectible {

    private boolean collected = false;

    /**
     * Creates a life source at the given position and size.
     * @param x      horizontal position
     * @param y      vertical position
     * @param width  life source width, must be greater than 0
     * @param height life source height, must be greater than 0
     * @param color  life source color
     */
    public LifeSource(double x, double y, double width, double height, String color) {
        super(x, y, width, height, color);
    }

    /**
     * Gives an extra life to the player.
     * @param player the player that collected this life source
     */
    @Override
    public void onCollect(Player player) {
        if (!collected) {
            collected = true;
            player.addLife();
        }
    }

    @Override
    public void onContact(Player player, Level level) {
        onCollect(player);
    }

    @Override
    public boolean shouldBeRemoved() {
        return collected;
    }

    @Override
    public String getFileType() {
        return "LIFESOURCE";
    }

    @Override
    public Color getDisplayColor() {
        return new Color(255, 105, 180);
    }

    @Override
    public DrawCommand toDrawCommand() {
        return new DrawCommand(getDisplayColor(), (int) getX(), (int) getY(), (int) getWidth(), (int) getHeight(),
                DrawCommand.Shape.OVAL, Color.BLACK);
    }
}
