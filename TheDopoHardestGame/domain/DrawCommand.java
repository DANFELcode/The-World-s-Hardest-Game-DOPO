package domain;

import java.awt.Color;

public class DrawCommand {

    public enum Shape { RECT, OVAL }

    public final Color color;
    public final int x, y, width, height;
    public final Shape shape;
    public final Color borderColor;

    public DrawCommand(Color color, int x, int y, int width, int height, Shape shape) {
        this(color, x, y, width, height, shape, null);
    }

    public DrawCommand(Color color, int x, int y, int width, int height, Shape shape, Color borderColor) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.shape = shape;
        this.borderColor = borderColor;
    }
}
