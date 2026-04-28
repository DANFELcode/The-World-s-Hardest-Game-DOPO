package domain;

/**
 * Representa un elemento movil en el juego <br>
 * <b>(x, y, velocidad, color)</b> <br>
 * <b>Inv:</b> velocidad > 0 
 */
public abstract class ElementoMovil {
    private int x;
    private int y;
    private double velocidad;
    private String color;

    /**
     * Crea un elemento movil con una posición inicial, velocidad y color
     * @param posición x horizontal inicial
     * @param posición y vertical inicial
     * @param velocidad del elemento movil debe ser mayor a 0
     * @param color del jugador o enemigo
     */
    public ElementoMovil(int x, int y, double velocidad, String color) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.color = color;
    }

    /**
     * Retorna la posición actual horizontal.
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Retorna la posición actual vertical.
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * Retorna la velocidad actual de movimiento.
     * @return velocidad del elemento
     */
    public double getVelocidad() {
        return velocidad;
    }

    /**
     * Retorna el color del elemento.
     * @return color como string
     */
    public String getColor() {
        return color;
    }

    /**
     * Actualiza la posición horizontal(disponible solo para subclases).
     * @param x nueva coordenada x
     */
    protected void setX(int x) {
        this.x = x;
    }

    /**
     * Actualiza la posición vertical(disponible solo para subclases).
     * @param y new y coordinatey nueva coordenada y
    protected void setY(int y) {
        this.y = y;
    }

    /**
     * Actualiza la velocidad del elemento.
     * @param nueva velocidad debe ser mayor a 0
     */
    protected void setVelocidad(double velocidad) {
        this.velocidad = velocidad;
    }
}
