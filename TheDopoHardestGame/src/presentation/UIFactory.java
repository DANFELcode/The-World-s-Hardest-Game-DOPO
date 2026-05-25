package presentation;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import javax.swing.JButton;
import javax.swing.JComponent;
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

    /**
     * Creates a modern minimalist "pill" button. White by default; when its
     * {@code flat-selected} client property is true it fills with the accent
     * color. Used as a toggle-style selector.
     * @param text the button label
     * @param accent the fill color when selected
     * @return the styled pill button
     */
    public static JButton createPillButton(String text, Color accent) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight(), arc = h;
                boolean selected = Boolean.TRUE.equals(getClientProperty("flat-selected"));
                boolean hover = getModel().isRollover() && isEnabled();

                Color fill, textColor, border;
                if (selected) {
                    fill = accent;
                    textColor = contrastText(accent);
                    border = accent;
                } else if (hover) {
                    fill = brighten(accent, 0.84f);
                    textColor = new Color(55, 60, 75);
                    border = brighten(accent, 0.45f);
                } else {
                    fill = new Color(255, 255, 255, 235);
                    textColor = new Color(70, 75, 90);
                    border = new Color(198, 203, 218);
                }

                g2.setColor(fill);
                g2.fillRoundRect(1, 1, w - 2, h - 2, arc, arc);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);

                g2.setFont(getFont());
                g2.setColor(textColor);
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        FontRenderContext frc = new FontRenderContext(null, true, true);
        TextLayout layout = new TextLayout(text, btn.getFont(), frc);
        int textWidth = (int) Math.ceil(layout.getBounds().getWidth());
        btn.setPreferredSize(new Dimension(Math.max(48, textWidth + 34), 38));
        btn.putClientProperty("flat-selected", Boolean.FALSE);
        return btn;
    }

    /**
     * Creates a round color-swatch button for picking a border color. Shows a
     * filled circle of {@code fill}; a blue ring marks the {@code flat-selected}
     * state and a disabled button is dimmed.
     * @param fill the swatch color
     * @return the styled circular button
     */
    public static JButton createColorButton(Color fill) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                int d = Math.min(w, h) - 12;
                int x = (w - d) / 2, y = (h - d) / 2;
                boolean selected = Boolean.TRUE.equals(getClientProperty("flat-selected"));
                boolean hover = getModel().isRollover() && isEnabled();

                if (selected) {
                    g2.setColor(new Color(40, 110, 230));
                    g2.fillOval(x - 5, y - 5, d + 10, d + 10);
                } else if (hover) {
                    g2.setColor(new Color(165, 188, 228));
                    g2.fillOval(x - 4, y - 4, d + 8, d + 8);
                }

                Composite old = g2.getComposite();
                if (!isEnabled()) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                }
                g2.setColor(fill);
                g2.fillOval(x, y, d, d);
                g2.setColor(new Color(90, 92, 105));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(x, y, d, d);
                g2.setComposite(old);

                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(46, 46));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("flat-selected", Boolean.FALSE);
        return btn;
    }

    /**
     * Creates a section title rendered with a flat styled look: a glossy
     * vertical gradient, a thin dark outline and a top sheen.
     * @param text the title text
     * @param baseColor the base fill color
     * @return a non-opaque component that paints the title centered
     */
    public static JComponent createStyledTitle(String text, Color baseColor) {
        return new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                int w = getWidth(), h = getHeight();
                Font font = new Font("Arial Black", Font.BOLD, 34);
                FontRenderContext frc = g2.getFontRenderContext();
                TextLayout layout = new TextLayout(text, font, frc);
                Rectangle2D bounds = layout.getBounds();

                double x = w / 2.0 - bounds.getCenterX();
                LineMetrics lm = font.getLineMetrics("Ay", frc);
                double y = h / 2.0 + (lm.getAscent() - lm.getDescent()) / 2.0;

                Shape shape = layout.getOutline(AffineTransform.getTranslateInstance(x, y));
                Rectangle2D sb = shape.getBounds2D();

                // Dark outline.
                g2.setColor(new Color(20, 20, 35));
                g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(shape);

                // Glossy vertical gradient fill.
                GradientPaint grad = new GradientPaint(
                    0, (float) sb.getMinY(), brighten(baseColor, 0.45f),
                    0, (float) sb.getMaxY(), baseColor.darker());
                g2.setPaint(grad);
                g2.fill(shape);

                // Top sheen clipped to the glyphs.
                Area sheen = new Area(shape);
                sheen.intersect(new Area(new Rectangle2D.Double(
                    sb.getX(), sb.getMinY(), sb.getWidth(), sb.getHeight() * 0.5)));
                g2.setPaint(new GradientPaint(
                    0, (float) sb.getMinY(), new Color(255, 255, 255, 150),
                    0, (float) (sb.getMinY() + sb.getHeight() * 0.5), new Color(255, 255, 255, 0)));
                g2.fill(sheen);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                FontRenderContext frc = new FontRenderContext(null, true, true);
                TextLayout layout = new TextLayout(text, new Font("Arial Black", Font.BOLD, 34), frc);
                Rectangle2D b = layout.getBounds();
                return new Dimension((int) Math.ceil(b.getWidth()) + 50,
                                     (int) Math.ceil(b.getHeight()) + 34);
            }
        };
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
