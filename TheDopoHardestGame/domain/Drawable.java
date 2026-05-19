package domain;
import dto.DrawCommand;

public interface Drawable {
    DrawCommand toDrawCommand();
}
