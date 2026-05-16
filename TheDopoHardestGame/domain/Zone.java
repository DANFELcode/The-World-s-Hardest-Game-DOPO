package domain;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * Represents a specific area within the level map. <br>
 * <b>(x, y, width, height, visited)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public abstract class Zone implements Interactable, Drawable, Serializable {

    protected double x;
    protected double y;
    protected double width;
    protected double height;
    private boolean visited;

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
        this.visited = false;
    }

    public abstract Color getDisplayColor();

    /**
     * Hook invoked when a player intersects this zone. Default behaviour: mark the zone as visited.
     * Subclasses can override to add custom behaviour.
     * @param player the player entering the zone
     */
    public void onPlayerEnter(Player player) {
        visit();
    }

    /**
     * Marks the zone as visited.
     */
    public void visit() {
        this.visited = true;
    }

    /**
     * Returns whether this zone has been visited by a player.
     * @return true if the zone has been visited
     */
    public boolean isVisited() {
        return visited;
    }

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
