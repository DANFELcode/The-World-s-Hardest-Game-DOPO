package domain;

import java.awt.geom.Rectangle2D;

/**
 * Represents a movable element in the game. <br>
 * <b>(x, y, width, height, speed)</b> <br>
 * <b>Inv:</b> speed > 0 and width > 0 and height > 0
 */
public abstract class MovableElement {
    private double x;
    private double y;
    private double width;
    private double height;
    private double speed;

    /** Domain calibration: how much an entity moves per tick when its speed multiplier is 1.0. */
    // Unidad del juego
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
    
    //getters se usan amplicamente porque 
    //toda colisión, movimiento, renderizado, persistencia y tests necesitan leer posición/tamaño/velocidad.
    /**
     * @return the current horizontal position
     */
    public double getX() { return x; }

    /**
     * @return the current vertical position
     */
    public double getY() { return y; }

    /**
     * @return the element width
     */
    public double getWidth() { return width; }

    /**
     * @return the element height
     */
    public double getHeight() { return height; }

    /**
     * @return the movement speed
     */
    public double getSpeed() { return speed; }

    /**
     * Updates the position of the element.
     * @param newX new horizontal position
     * @param newY new vertical position
     */
    public void setPosition(double newX, double newY) {
        this.x = newX;
        this.y = newY;
    }
    
    /**
     * Returns the rectangular collision area occupied by this element.
     * @return a rectangle at the element's current position with its width and height
     */
    public Rectangle2D getAreaColision() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    /**
     * Updates the movement speed. Values not greater than 0 are ignored to preserve the class invariant.
     * @param speed new movement speed
     */
    //lo usan las skins para cambiar de velocidad a player
    protected void setSpeed(double speed) {
        if (speed > 0) this.speed = speed;
    }

    /**
     * Updates the element width. Values not greater than 0 are ignored to preserve the class invariant.
     * @param width new element width
     */
    //lo usan las skins para cambiar de tamaño a player
    
    protected void setWidth(double width) {
        if (width > 0) this.width = width;
    }

    /**
     * Updates the element height. Values not greater than 0 are ignored to preserve the class invariant.
     * @param height new element height
     */
    protected void setHeight(double height) {
        if (height > 0) this.height = height;
    }


}
