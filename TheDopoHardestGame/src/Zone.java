package domain;

/**
 * Representa un área específica dentro del mapa del nivel.
 */
public abstract class Zone {
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    public Zone(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
}