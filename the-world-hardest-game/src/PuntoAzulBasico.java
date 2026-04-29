package domain;

import java.awt.Rectangle;

/**
 * Representa el enemigo básico del juego. <br>
 * <b>(x, y, velocidad=1.0, color="azul", horizontal, sentido)</b> <br>
 * <b>Inv:</b> sentido == 1 or sentido == -1
 */
public class PuntoAzulBasico extends Enemigo {
	private boolean horizontal;
	private int sentido;

	/**
	 * Crea un enemigo básico con posición, dirección y sentido de movimiento definidos.
	 * @param x posición horizontal inicial
	 * @param y posición vertical inicial
	 * @param horizontal true si se mueve horizontalmente, false si se mueve verticalmente
	 * @param sentido dirección de movimiento: 1 hacia adelante, -1 hacia atrás
	 */
    public PuntoAzulBasico(double x, double y, boolean horizontal, int sentido) {
        super(x, y, 1.0, "azul");
        this.horizontal = horizontal;
        this.sentido = sentido;
    }

    /**
     * Desplaza al enemigo en línea recta, rebotando al encontrar una pared.
     * @param mapa mapa del nivel para verificar colisiones con paredes
     */
    @Override
    public void mover(Mapa mapa) {
        double nuevaX = getX() + (horizontal ? sentido * getVelocidad() : 0);
        double nuevaY = getY() + (horizontal ? 0 : sentido * getVelocidad());

        if (mapa.hayPared(nuevaX, nuevaY)) {
            sentido *= -1;
        } else {
            setX(nuevaX);
            setY(nuevaY);
        }
    }

    /**
     * Retorna el área de colisión del enemigo.
     * @return rectángulo de 20x20 píxeles en la posición actual
     * pendiente por ajustar el 20x20
     */
    @Override
    public Rectangle getAreaColision() {
    	return new Rectangle((int) getX(), (int) getY(), 20, 20);
    }

    /**
     * Al colisionar con un jugador, lo mata.
     * @param jugador jugador con el que colisiona
     */
    @Override
    public void alColisionar(Jugador jugador) {
    	jugador.morir();
    }
}
