package presentation;

import dto.DrawCommand;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Stateless renderer for game elements. Draws a {@link DrawCommand} onto a
 * Graphics2D with gradients, gloss and shape-specific styling. Shared by the
 * game board and the tutorial previews so both render elements identically.
 */
public final class ElementRenderer {

    private ElementRenderer() { }

    /** Draws a single element, dispatching on its shape. */
    public static void drawElement(Graphics2D g2, DrawCommand cmd, Stroke originalStroke) {
        switch (cmd.shape) {
            case BOMB:   drawBomb(g2, cmd, originalStroke);         break;
            case PLAYER: drawPlayer(g2, cmd, originalStroke);       break;
            case OVAL:   drawGlossyOval(g2, cmd, originalStroke);   break;
            default:     drawGradientRect(g2, cmd, originalStroke); break;
        }
    }

    private static void drawPlayer(Graphics2D g2, DrawCommand cmd, Stroke originalStroke) {
        int arc = Math.max(4, Math.min(cmd.width, cmd.height) / 4);
        GradientPaint gradient = new GradientPaint(
            cmd.x, cmd.y,                   brighter(cmd.color, 0.45f),
            cmd.x, cmd.y + cmd.height,      darker(cmd.color, 0.75f));
        g2.setPaint(gradient);
        g2.fillRoundRect(cmd.x, cmd.y, cmd.width, cmd.height, arc, arc);

        // Subtle top sheen
        g2.setColor(new Color(255, 255, 255, 50));
        g2.fillRoundRect(cmd.x + 2, cmd.y + 2, cmd.width - 4, cmd.height / 3, arc, arc);

        if (cmd.borderColor != null) {
            g2.setColor(cmd.borderColor);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(cmd.x, cmd.y, cmd.width, cmd.height, arc, arc);
            g2.setStroke(originalStroke);
        }
        if (cmd.outerBorderColor != null) {
            g2.setColor(cmd.outerBorderColor);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(cmd.x - 3, cmd.y - 3, cmd.width + 6, cmd.height + 6, arc, arc);
            g2.setStroke(originalStroke);
        }
    }

    private static void drawGlossyOval(Graphics2D g2, DrawCommand cmd, Stroke originalStroke) {
        float cx = cmd.x + cmd.width / 2f;
        float cy = cmd.y + cmd.height / 2f;
        float radius = Math.max(1, Math.min(cmd.width, cmd.height) / 2f);

        // Focus in upper-left quadrant, always inside radius
        float focusX = cx - radius * 0.38f;
        float focusY = cy - radius * 0.38f;

        RadialGradientPaint paint = new RadialGradientPaint(
            new Point2D.Float(cx, cy), radius,
            new Point2D.Float(focusX, focusY),
            new float[]{0f, 0.5f, 1f},
            new Color[]{brighter(cmd.color, 0.6f), cmd.color, darker(cmd.color, 0.72f)},
            MultipleGradientPaint.CycleMethod.NO_CYCLE);
        g2.setPaint(paint);
        g2.fillOval(cmd.x, cmd.y, cmd.width, cmd.height);

        // Small white sheen at the top
        g2.setColor(new Color(255, 255, 255, 70));
        g2.fillOval(cmd.x + cmd.width / 4, cmd.y + cmd.height / 8,
                    cmd.width / 2, cmd.height / 4);

        if (cmd.borderColor != null) {
            g2.setColor(cmd.borderColor);
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(cmd.x, cmd.y, cmd.width, cmd.height);
            g2.setStroke(originalStroke);
        }
        if (cmd.outerBorderColor != null) {
            g2.setColor(cmd.outerBorderColor);
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(cmd.x - 3, cmd.y - 3, cmd.width + 6, cmd.height + 6);
            g2.setStroke(originalStroke);
        }
    }

    private static void drawGradientRect(Graphics2D g2, DrawCommand cmd, Stroke originalStroke) {
        GradientPaint gradient = new GradientPaint(
            cmd.x, cmd.y,                   brighter(cmd.color, 0.3f),
            cmd.x, cmd.y + cmd.height,      darker(cmd.color, 0.82f));
        g2.setPaint(gradient);
        g2.fillRect(cmd.x, cmd.y, cmd.width, cmd.height);

        if (cmd.borderColor != null) {
            g2.setColor(cmd.borderColor);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(cmd.x, cmd.y, cmd.width, cmd.height);
            g2.setStroke(originalStroke);
        }
        if (cmd.outerBorderColor != null) {
            g2.setColor(cmd.outerBorderColor);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(cmd.x - 3, cmd.y - 3, cmd.width + 6, cmd.height + 6);
            g2.setStroke(originalStroke);
        }
    }

    private static void drawBomb(Graphics2D g2, DrawCommand cmd, Stroke originalStroke) {
        int x = cmd.x, y = cmd.y, w = cmd.width, h = cmd.height;

        // Body: dark circle with a subtle highlight to give volume
        g2.setColor(new Color(30, 30, 30));
        g2.fillOval(x, y, w, h);
        g2.setColor(new Color(75, 75, 75));
        g2.fillOval(x + w / 5, y + h / 5, w / 3, h / 3);

        // Fuse: brown line from the top-right of the body going up-right
        int fsX = x + (int)(w * 0.68);
        int fsY = y + (int)(h * 0.18);
        int feX = x + w + (int)(w * 0.22);
        int feY = y - (int)(h * 0.32);
        g2.setColor(new Color(139, 90, 43));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(fsX, fsY, feX, feY);
        g2.setStroke(originalStroke);

        // Spark at the fuse tip
        int spark = Math.max(4, w / 5);
        g2.setColor(Color.ORANGE);
        g2.fillOval(feX - spark / 2, feY - spark / 2, spark, spark);
        g2.setColor(Color.YELLOW);
        int inner = Math.max(2, spark / 2);
        g2.fillOval(feX - inner / 2, feY - inner / 2, inner, inner);
    }

    private static Color brighter(Color c, float factor) {
        int r = Math.min(255, (int)(c.getRed()   + (255 - c.getRed())   * factor));
        int g = Math.min(255, (int)(c.getGreen() + (255 - c.getGreen()) * factor));
        int b = Math.min(255, (int)(c.getBlue()  + (255 - c.getBlue())  * factor));
        return new Color(r, g, b, c.getAlpha());
    }

    private static Color darker(Color c, float factor) {
        return new Color(
            Math.max(0, (int)(c.getRed()   * factor)),
            Math.max(0, (int)(c.getGreen() * factor)),
            Math.max(0, (int)(c.getBlue()  * factor)),
            c.getAlpha());
    }
}
