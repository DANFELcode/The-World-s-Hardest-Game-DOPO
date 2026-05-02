package domain;

/**
 * Clase base abstracta para todos los elementos que pueden moverse en el juego.
 */
public abstract class MovableElement {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected double speed;

    public MovableElement(double x, double y, double width, double height, double speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }

    /**
     * Actualiza las coordenadas del elemento en el mapa.
     * 
     * @param newX la nueva coordenada X
     * @param newY la nueva coordenada Y
     */
    public void setPosition(double newX, double newY) {
        this.x = newX;
        this.y = newY;
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

    public double getSpeed() {
        return speed;
    }
}
