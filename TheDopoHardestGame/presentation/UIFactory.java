package presentation;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Factory of styled Swing components for the presentation layer.
 * Centralizes the custom rendering (3D buttons, gradient panels) so the
 * windows/panels stay focused on layout and behaviour, not pixel math.
 */
public final class UIFactory {

    private UIFactory() { }

    /**
     * Creates a large 3D-extruded button with a vertical gradient fill and a
     * black outline. Width adapts to the text plus lateral padding.
     * @param text the button label
     * @param fillColor the base fill color
     * @return the styled button
     */
    public static JButton createStyledButton(String text, Color fillColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                int w = getWidth(), h = getHeight();

                // Fondo redondeado sutil cuando hover
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 70));
                    g2.fillRoundRect(0, 0, w, h, 24, 24);
                }

                // Arial Black (más grueso que Arial Bold)
                Font font = new Font("Arial Black", Font.BOLD, 42);
                FontRenderContext frc = g2.getFontRenderContext();
                TextLayout layout = new TextLayout(getText(), font, frc);
                Rectangle2D bounds = layout.getBounds();

                // Centrado horizontal por bounds del texto
                double x = w / 2.0 - bounds.getCenterX();
                // Centrado vertical por métricas del font (consistente con o sin acentos)
                LineMetrics lm = font.getLineMetrics("Ay", frc);
                double y = h / 2.0 + (lm.getAscent() - lm.getDescent()) / 2.0;

                AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
                Shape shape = layout.getOutline(transform);

                // Extrusión 3D: copias offset hacia abajo-derecha en versión oscura del color
                Color deep = fillColor.darker().darker();
                int depth = 3;
                for (int i = depth; i >= 1; i--) {
                    AffineTransform extrudeTr =
                        AffineTransform.getTranslateInstance(x + i * 0.7, y + i * 0.7);
                    Shape extrudeShape = layout.getOutline(extrudeTr);
                    float t = i / (float) depth;
                    int r = (int) (fillColor.getRed()   * (1 - t) * 0.4 + deep.getRed()   * t * 0.6);
                    int gg = (int) (fillColor.getGreen() * (1 - t) * 0.4 + deep.getGreen() * t * 0.6);
                    int b = (int) (fillColor.getBlue()  * (1 - t) * 0.4 + deep.getBlue()  * t * 0.6);
                    g2.setColor(new Color(Math.min(255, r), Math.min(255, gg), Math.min(255, b)));
                    g2.fill(extrudeShape);
                }

                // Gradiente vertical del fill (más claro arriba, más oscuro abajo)
                Color top = getModel().isPressed() ? fillColor.darker()
                          : getModel().isRollover() ? brighten(fillColor, 0.25f)
                          : brighten(fillColor, 0.15f);
                Color bot = getModel().isPressed() ? fillColor.darker().darker()
                          : fillColor;

                Rectangle2D sb = shape.getBounds2D();
                GradientPaint grad = new GradientPaint(
                    0, (float) sb.getMinY(), top,
                    0, (float) sb.getMaxY(), bot);
                g2.setPaint(grad);
                g2.fill(shape);

                // Outline negro delgado
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(shape);

                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        // Calcular ancho del botón según el texto real + padding lateral
        Font measureFont = new Font("Arial Black", Font.BOLD, 42);
        FontRenderContext frc = new FontRenderContext(null, true, true);
        TextLayout layout = new TextLayout(text, measureFont, frc);
        int textWidth = (int) Math.ceil(layout.getBounds().getWidth());
        btn.setPreferredSize(new Dimension(textWidth + 60, 90));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Creates a panel painted with a soft top-to-bottom gradient background.
     * @param layout the layout manager for the panel
     * @return the gradient panel
     */
    public static JPanel createGradientPanel(LayoutManager layout) {
        return new GradientPanel(layout);
    }

    /**
     * Creates a flat button that highlights on hover and when its
     * {@code flat-selected} client property is true.
     * @param text the button label
     * @param hoverColor the highlight color
     * @return the flat button
     */
    public static JButton createFlatButton(String text, Color hoverColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                boolean selected = Boolean.TRUE.equals(getClientProperty("flat-selected"));
                boolean hover = getModel().isRollover() && isEnabled();

                Color bg;
                Color fg;
                int borderW;
                if (selected) {
                    bg = hoverColor;
                    fg = contrastText(hoverColor);
                    borderW = 4;
                } else if (hover) {
                    bg = hoverColor;
                    fg = contrastText(hoverColor);
                    borderW = 2;
                } else {
                    bg = Color.WHITE;
                    fg = Color.BLACK;
                    borderW = 2;
                }

                g2.setColor(bg);
                g2.fillRect(0, 0, w, h);

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(borderW));
                g2.drawRect(borderW / 2, borderW / 2, w - borderW, h - borderW);

                g2.setFont(getFont());
                g2.setColor(fg);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(getText())) / 2;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);

                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setMargin(new Insets(8, 16, 8, 16));
        btn.putClientProperty("flat-selected", Boolean.FALSE);
        return btn;
    }

    /** Returns a lighter version of the color, blended toward white by {@code amount} (0..1). */
    private static Color brighten(Color c, float amount) {
        int r = Math.min(255, (int) (c.getRed()   + (255 - c.getRed())   * amount));
        int g = Math.min(255, (int) (c.getGreen() + (255 - c.getGreen()) * amount));
        int b = Math.min(255, (int) (c.getBlue()  + (255 - c.getBlue())  * amount));
        return new Color(r, g, b);
    }

    /** Returns black or white, whichever contrasts better against the given background. */
    private static Color contrastText(Color bg) {
        int brightness = (bg.getRed() * 299 + bg.getGreen() * 587 + bg.getBlue() * 114) / 1000;
        return brightness > 150 ? Color.BLACK : Color.WHITE;
    }
}
