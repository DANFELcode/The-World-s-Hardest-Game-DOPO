package domain;

import java.awt.Rectangle;

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

    /**
     * Creates a static element with a position and size.
     * @param x initial horizontal position
     * @param y initial vertical position
     * @param width element width, must be greater than 0
     * @param height element height, must be greater than 0
     */
    public StaticElement(double x, double y, double width, double height, String color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    /**
     * Returns the collision area of the element.
     * @return rectangle representing the collision bounds
     */
    public Rectangle getAreaColision() {
        return new Rectangle((int) x, (int) y, (int) width, (int) height);
    }    
    /**
     * Returns whether this element blocks movement.
     * @return false by default, overridden by blocking elements
     */
    public boolean isBlocking() {
        return false;
    }

    public double getX() {
    	return x;
    	}
    public double getY() {
    	return y;
    	}
    public double getWidth() {
    	return width;
    	}
    public double getHeight() {
    	return height;
    	}
    public String getColor() {
    	return color;
    }

    public void setX(double x) {
    	this.x = x;
    	}
    public void setY(double y) {
    	this.y = y;
    	}
    
    
}
