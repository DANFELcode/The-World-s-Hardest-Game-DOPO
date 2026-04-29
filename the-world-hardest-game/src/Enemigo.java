package domain;

import java.awt.Rectangle;

/**
 * Representa un enemigo en el juego. <br>
 * <b>(x, y, velocidad, color)</b> <br>
 * <b>Inv:</b> velocidad > 0
 */
public abstract class Enemigo extends ElementoMovil implements Colisionable {

    /**
     * Crea un enemigo con posición, velocidad y color definidos.
     * @param x posición horizontal inicial
     * @param y posición vertical inicial
     * @param velocidad velocidad de movimiento, debe ser mayor a 0
     * @param color color visual del enemigo
     */
    public Enemigo(double x, double y, double velocidad, String color) {
        super(x, y, velocidad, color);
    }

    /**
     * Desplaza al enemigo según su patrón de movimiento.
     */
    public abstract void mover();

    /**
     * Retorna el área de colisión del enemigo.
     * @return rectángulo que representa el área de colisión
     */
    @Override
    public abstract Rectangle getAreaColision();

    /**
     * Define la reacción al colisionar con un jugador.
     * @param j jugador con el que colisiona
     */
    @Override
    public abstract void alColisionar(Jugador jugador);
}
