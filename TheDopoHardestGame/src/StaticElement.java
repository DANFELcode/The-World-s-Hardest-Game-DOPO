package domain;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

/**
 * Represents a static element in the game. <br>
 * <b>(x, y, width, height)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public abstract class StaticElement {
    private double x;
    private double y;
    private double width;
    private double height;
    private String color;

    public StaticElement(double x, double y, double width, double height, String color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    /**
     * Called when a player makes contact with this static element.
     */
    public void onContact(Player player, Level level) { }

    /**
     * Called when an enemy makes contact with this static element.
     */
    public void onContact(Enemy enemy, Level level) { 
    	
    }

    public Rectangle2D getAreaColision() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public boolean isBomb() { return false; }
    public boolean shouldBeRemoved() { return false; }
    public boolean isBlocking() { return false; }
    public Color getDisplayColor() { return Color.BLACK; }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public String getColor() { return color; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}
