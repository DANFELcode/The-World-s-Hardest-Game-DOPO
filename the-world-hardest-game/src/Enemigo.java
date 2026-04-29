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
    public abstract void mover(Mapa mapa);

    /**
     * Retorna el área de colisión del enemigo.
     * @return rectángulo de 20x20 píxeles en la posición actual, pendiente ajustar al tamaño real
     */
    @Override
    public Rectangle getAreaColision() {
        return new Rectangle((int) getX(), (int) getY(), 20, 20);
    }

    /**
     * Define la reacción al colisionar con un jugador.
     * @param j jugador con el que colisiona
     */
    @Override
    public abstract void alColisionar(Jugador jugador);
}
