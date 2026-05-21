package presentation;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

/**
 * Start view: the 3D-rendered game title and the main menu buttons
 * (JUGAR / CONFIGURACIÓN).
 */
public class InicioPanel extends GradientPanel {

    private final MainView host;

    /**
     * @param host the main window, used for navigation
     */
    public InicioPanel(MainView host) {
        super(new BorderLayout());
        this.host = host;
        construirUI();
    }

    private void construirUI() {
        JPanel grupoArriba = new JPanel();
        grupoArriba.setLayout(new BoxLayout(grupoArriba, BoxLayout.Y_AXIS));
        grupoArriba.setOpaque(false);

        JComponent tituloDibujado = crearTitulo();
        tituloDibujado.setOpaque(false);
        tituloDibujado.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setOpaque(false);
        panelBotones.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton playGame = UIFactory.createStyledButton("JUGAR", new Color(0xEB, 0x55, 0x55));
        JButton settings = UIFactory.createStyledButton("CONFIGURACIÓN", new Color(0x4C, 0xC1, 0x6D));
        playGame.addActionListener(e -> host.mostrarExplicacion());

        panelBotones.add(playGame);
        panelBotones.add(settings);

        grupoArriba.add(Box.createVerticalStrut(40));
        grupoArriba.add(tituloDibujado);
        grupoArriba.add(Box.createVerticalStrut(40));
        grupoArriba.add(panelBotones);

        add(grupoArriba, BorderLayout.CENTER);
    }

    /** Builds the component that renders the 3D "THE DOPO... HARDEST GAME" title. */
    private JComponent crearTitulo() {
        return new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();

                // Calcular primero la posición y ancho del título grande
                Font bigFont = new Font("Impact", Font.PLAIN, 95);
                FontRenderContext frc = g2.getFontRenderContext();
                String big = "HARDEST GAME";
                TextLayout layout = new TextLayout(big, bigFont, frc);

                double baseW = layout.getBounds().getWidth();
                double maxW = w * 0.92;
                double scaleX = Math.min(1.8, maxW / baseW);
                double textW = baseW * scaleX;
                double bigX = (w - textW) / 2.0;
                double bigY = 175;

                // Línea pequeña: alineada al borde izquierdo del título grande
                Font smallFont = new Font("Arial", Font.BOLD, 32);
                g2.setFont(smallFont);
                g2.setColor(Color.BLACK);
                String small = "THE DOPO...";
                g2.drawString(small, (int) bigX, 75);

                AffineTransform transform = new AffineTransform();
                transform.translate(bigX, bigY);
                transform.scale(scaleX, 1.0);
                Shape outline = layout.getOutline(transform);

                Rectangle2D shapeBounds = outline.getBounds2D();
                float minY = (float) shapeBounds.getMinY();
                float maxY = (float) shapeBounds.getMaxY();

                // Extrusión 3D: copias offset hacia abajo-derecha en azul oscuro
                int depth = 10;
                for (int i = depth; i >= 1; i--) {
                    AffineTransform extrudeTr =
                        AffineTransform.getTranslateInstance(i * 0.8, i * 0.8);
                    Shape extrudeShape = extrudeTr.createTransformedShape(outline);
                    float t = i / (float) depth;
                    int r = (int) (30 + (10 - 30) * (1 - t));
                    int g3 = (int) (50 + (25 - 50) * (1 - t));
                    int b = (int) (100 + (60 - 100) * (1 - t));
                    g2.setColor(new Color(r, g3, b));
                    g2.fill(extrudeShape);
                }

                // Doble contorno alrededor de la cara frontal: negro grueso + blanco encima
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(outline);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(outline);

                // Gradiente glossy: azul grisáceo débil arriba → azul medio → más oscuro abajo
                LinearGradientPaint gradient = new LinearGradientPaint(
                    0, minY, 0, maxY,
                    new float[]{0f, 0.45f, 1f},
                    new Color[]{
                        new Color(0x9A, 0xB1, 0xCC),
                        new Color(0x6E, 0xA0, 0xE6),
                        new Color(0x46, 0x79, 0xBE)
                    }
                );
                g2.setPaint(gradient);
                g2.fill(outline);

                // Brillo superior sutil
                Rectangle2D sheenRect = new Rectangle2D.Double(
                    shapeBounds.getX(), minY, shapeBounds.getWidth(),
                    shapeBounds.getHeight() * 0.45);
                Area sheenClip = new Area(outline);
                sheenClip.intersect(new Area(sheenRect));
                LinearGradientPaint sheen = new LinearGradientPaint(
                    0, minY, 0, (float)(minY + shapeBounds.getHeight() * 0.45),
                    new float[]{0f, 1f},
                    new Color[]{
                        new Color(255, 255, 255, 120),
                        new Color(255, 255, 255, 0)
                    }
                );
                g2.setPaint(sheen);
                g2.fill(sheenClip);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 220);
            }
        };
    }
}
