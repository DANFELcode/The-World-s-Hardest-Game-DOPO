package domain;
import dto.DrawCommand;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

/**
 * Represents a specific area within the level map. <br>
 * <b>(x, y, width, height)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public abstract class Zone implements Interactable, Drawable {

    protected double x;
    protected double y;
    protected double width;
    protected double height;

    /**
     * Creates a zone at the given position and size.
     * @param x horizontal position
     * @param y vertical position
     * @param width zone width, must be greater than 0
     * @param height zone height, must be greater than 0
     */
    public Zone(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract Color getDisplayColor();

    /**
     * Hook invoked when a player intersects this zone.
     * Subclasses override to add custom behaviour (checkpoint, victory check, etc).
     * @param player the player entering the zone
     */
    public void onPlayerEnter(Player player) { }

    @Override
    public void onPlayerContact(Player player, Level level) {
        onPlayerEnter(player);
    }

    @Override
    public DrawCommand toDrawCommand() {
        return new DrawCommand(getDisplayColor(), (int)x, (int)y, (int)width, (int)height, DrawCommand.Shape.RECT);
    }

    @Override
    public Rectangle2D getAreaColision() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

}
