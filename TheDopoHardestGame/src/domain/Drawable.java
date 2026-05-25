package domain;
import dto.DrawCommand;

/**
 * Element que se puede dibujar. Construye su propio DrawCommand para que la presentacion
 * lo pinte sin acceder al dominio directamente.
 */
public interface Drawable {
    DrawCommand toDrawCommand();
}
