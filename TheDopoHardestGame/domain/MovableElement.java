package domain;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * Represents a movable element in the game. <br>
 * <b>(x, y, width, height, speed)</b> <br>
 * <b>Inv:</b> speed > 0 and width > 0 and height > 0
 */
public abstract class MovableElement implements Serializable {
    private double x;
    private double y;
    private double width;
    private double height;
    private double speed;

    /** Domain calibration: how much an entity moves per tick when its speed multiplier is 1.0. */
    protected static final double UNIT = 3.0;

    /**
     * Creates a movable element with an initial position, size and speed.
     * @param x initial horizontal position
     * @param y initial vertical position
     * @param width element width, must be greater than 0
     * @param height element height, must be greater than 0
     * @param speed movement speed, must be greater than 0
     */
    public MovableElement(double x, double y, double width, double height, double speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }

    /**
     * Updates the position of the element.
     * @param newX new horizontal position
     * @param newY new vertical position
     */
    public void setPosition(double newX, double newY) {
        this.x = newX;
        this.y = newY;
    }
    
    public Rectangle2D getAreaColision() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    protected void setSpeed(double speed) {
        if (speed > 0) this.speed = speed;
    }

    protected void setWidth(double width) {
        if (width > 0) this.width = width;
    }

    protected void setHeight(double height) {
        if (height > 0) this.height = height;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getSpeed() { return speed; }
}
