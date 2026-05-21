package presentation;

import domain.GameConstants;
import dto.DrawCommand;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Explanation view: a multi-slide mini-tutorial. Each slide explains game
 * components with a live preview graphic on top and a description below.
 * Navigation is done through a row of clickable slider dots.
 */
public class ExpPanel extends GradientPanel {

    private static final String FONT = "Segoe UI";
    private static final int SLIDE_COUNT = 4;
    private static final Color RED_PLAYER  = new Color(225, 55, 55);
    private static final Color BLUE_PLAYER = new Color(55, 80, 225);

    private final MainView host;
    private final CardLayout slidesLayout = new CardLayout();
    private final JPanel slides = new JPanel(slidesLayout);
    private SlideDots dots;
    private int currentSlide = 0;

    /**
     * @param host the main window, used for navigation
     */
    public ExpPanel(MainView host) {
        super(new BorderLayout());
        this.host = host;
        construirUI();
    }

    private void construirUI() {
        add(construirSlides(), BorderLayout.CENTER);
        add(construirBarraInferior(), BorderLayout.SOUTH);
        mostrarSlide(0);
    }

    private JPanel construirSlides() {
        slides.setOpaque(false);

        slides.add(crearSlide(
            tarjeta(previewJugadores(), "Jugadores",
                "Eres un cuadrado. <b>Rojo</b>: velocidad normal. "
                + "<b>Azul</b>: 1.5× más rápido y grande. "
                + "<b>Verde</b>: absorbe el primer golpe pero pierde velocidad."),
            tarjeta(previewEnemigos(), "Enemigos",
                "Esferas azules letales. Las hay <b>normales</b>, "
                + "<b>aceleradas</b> (2× veloces) y <b>patrulleras</b> "
                + "(siguen una ruta). Tocarlas es muerte instantánea.")), "0");

        slides.add(crearSlide(
            tarjeta(previewMonedas(), "Monedas",
                "Esferas doradas. Debes recolectar <b>todas</b> las monedas "
                + "del nivel antes de poder ganar."),
            tarjeta(previewZonaFinal(), "Zona final",
                "El rectángulo verde oscuro. Llega aquí con todas "
                + "las monedas recogidas para <b>completar el nivel</b>.")), "1");

        slides.add(crearSlide(
            tarjeta(previewCheckpoint(), "Zona segura",
                "Zona verde claro. Dentro estás a salvo de enemigos y se "
                + "vuelve tu punto de <b>respawn</b>: si mueres, reapareces aquí."),
            tarjeta(previewVidaExtra(), "Vida extra",
                "Esfera rosa. Otorga una <b>vida extra</b> que absorbe un "
                + "golpe sin que mueras.")), "2");

        slides.add(crearSlide(
            tarjeta(previewBomba(), "Bombas",
                "Explotan al contacto. Matan al jugador (si sobrevive el "
                + "golpe, lo empujan hacia atrás) y destruyen enemigos."),
            tarjeta(previewMonedasSkin(), "Monedas skin",
                "Esferas de color. Al recogerlas cambias de <b>tipo de "
                + "jugador</b> (rojo, azul o verde) y adoptas sus "
                + "habilidades. La skin vuelve a la normal al morir.")), "3");

        return slides;
    }

    private JPanel construirBarraInferior() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        dots = new SlideDots(SLIDE_COUNT);
        JPanel dotsWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
        dotsWrap.setOpaque(false);
        dotsWrap.add(dots);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 16));
        botones.setOpaque(false);
        JButton volver = UIFactory.createStyledButton("VOLVER", new Color(0x9A, 0x4B, 0xC1));
        JButton jugar  = UIFactory.createStyledButton("JUGAR", new Color(0xEB, 0x55, 0x55));
        volver.addActionListener(e -> host.mostrarInicio());
        jugar.addActionListener(e -> host.mostrarSeleccion());
        botones.add(volver);
        botones.add(jugar);

        bottom.add(dotsWrap, BorderLayout.NORTH);
        bottom.add(botones, BorderLayout.CENTER);
        return bottom;
    }

    /** Switches to the slide at the given index and updates the dots. */
    private void mostrarSlide(int idx) {
        if (idx < 0 || idx >= SLIDE_COUNT) return;
        currentSlide = idx;
        slidesLayout.show(slides, String.valueOf(idx));
        dots.setActive(idx);
    }

    private JPanel crearSlide(JComponent izquierda, JComponent derecha) {
        JPanel slide = new JPanel(new GridLayout(1, 2, 30, 0));
        slide.setOpaque(false);
        slide.setBorder(BorderFactory.createEmptyBorder(15, 60, 10, 60));
        slide.add(izquierda);
        slide.add(derecha);
        return slide;
    }

    /** Builds one component card: preview graphic on top, title and description below. */
    private JPanel tarjeta(JComponent preview, String titulo, String descripcion) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(255, 255, 255, 165));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(120, 120, 175), 2),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        preview.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tit = new JLabel(titulo);
        tit.setFont(new Font(FONT, Font.BOLD, 25));
        tit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel desc = new JLabel("<html><div style='text-align:center; width:260px'>"
            + descripcion + "</div></html>");
        desc.setFont(new Font(FONT, Font.PLAIN, 16));
        desc.setHorizontalAlignment(SwingConstants.CENTER);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(preview);
        card.add(Box.createVerticalStrut(16));
        card.add(tit);
        card.add(Box.createVerticalStrut(10));
        card.add(desc);
        card.add(Box.createVerticalGlue());
        return card;
    }

    // --- Component previews -------------------------------------------------

    private Preview previewJugadores() {
        List<DrawCommand> l = new ArrayList<>();
        l.add(new DrawCommand(RED_PLAYER, 35, 58, 44, 44, DrawCommand.Shape.PLAYER, Color.BLACK));
        l.add(new DrawCommand(BLUE_PLAYER, 102, 48, 56, 56, DrawCommand.Shape.PLAYER, Color.BLACK));
        l.add(new DrawCommand(GameConstants.COLOR_GREEN_NORMAL, 182, 58, 44, 44, DrawCommand.Shape.PLAYER, Color.BLACK));
        return new Preview(l);
    }

    private Preview previewEnemigos() {
        List<DrawCommand> l = new ArrayList<>();
        l.add(new DrawCommand(GameConstants.COLOR_ENEMY, 40, 53, 44, 44, DrawCommand.Shape.OVAL));
        l.add(new DrawCommand(GameConstants.COLOR_ENEMY, 108, 53, 44, 44, DrawCommand.Shape.OVAL));
        l.add(new DrawCommand(GameConstants.COLOR_ENEMY, 176, 53, 44, 44, DrawCommand.Shape.OVAL));
        return new Preview(l);
    }

    private Preview previewMonedas() {
        List<DrawCommand> l = new ArrayList<>();
        l.add(new DrawCommand(GameConstants.COLOR_COIN, 50, 57, 36, 36, DrawCommand.Shape.OVAL));
        l.add(new DrawCommand(GameConstants.COLOR_COIN, 112, 57, 36, 36, DrawCommand.Shape.OVAL));
        l.add(new DrawCommand(GameConstants.COLOR_COIN, 174, 57, 36, 36, DrawCommand.Shape.OVAL));
        return new Preview(l);
    }

    private Preview previewZonaFinal() {
        List<DrawCommand> l = new ArrayList<>();
        l.add(new DrawCommand(GameConstants.COLOR_FINAL_ZONE, 70, 38, 120, 78, DrawCommand.Shape.RECT));
        return new Preview(l);
    }

    private Preview previewCheckpoint() {
        List<DrawCommand> l = new ArrayList<>();
        l.add(new DrawCommand(GameConstants.COLOR_INTERMEDIATE_ZONE, 70, 38, 120, 78, DrawCommand.Shape.RECT));
        return new Preview(l);
    }

    private Preview previewVidaExtra() {
        List<DrawCommand> l = new ArrayList<>();
        l.add(new DrawCommand(GameConstants.COLOR_LIFESOURCE, 100, 45, 60, 60, DrawCommand.Shape.OVAL));
        return new Preview(l);
    }

    private Preview previewBomba() {
        List<DrawCommand> l = new ArrayList<>();
        l.add(new DrawCommand(GameConstants.COLOR_BOMB, 105, 68, 50, 50, DrawCommand.Shape.BOMB));
        return new Preview(l);
    }

    private Preview previewMonedasSkin() {
        List<DrawCommand> l = new ArrayList<>();
        l.add(new DrawCommand(RED_PLAYER, 40, 53, 44, 44, DrawCommand.Shape.OVAL));
        l.add(new DrawCommand(BLUE_PLAYER, 108, 53, 44, 44, DrawCommand.Shape.OVAL));
        l.add(new DrawCommand(GameConstants.COLOR_GREEN_NORMAL, 176, 53, 44, 44, DrawCommand.Shape.OVAL));
        return new Preview(l);
    }

    /**
     * Small component that paints a fixed set of DrawCommands via the shared
     * renderer. The content is auto-centered: its bounding box is computed and
     * translated so it sits exactly in the middle of the component.
     */
    private static class Preview extends JComponent {
        private final List<DrawCommand> cmds;

        Preview(List<DrawCommand> cmds) {
            this.cmds = cmds;
            setPreferredSize(new Dimension(260, 140));
            setMaximumSize(new Dimension(260, 140));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
            double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
            for (DrawCommand c : cmds) {
                double x0 = c.x, y0 = c.y, x1 = c.x + c.width, y1 = c.y + c.height;
                if (c.shape == DrawCommand.Shape.BOMB) {
                    x1 += c.width * 0.32;
                    y0 -= c.height * 0.45;
                }
                minX = Math.min(minX, x0);
                minY = Math.min(minY, y0);
                maxX = Math.max(maxX, x1);
                maxY = Math.max(maxY, y1);
            }
            g2.translate(getWidth() / 2.0 - (minX + maxX) / 2.0,
                         getHeight() / 2.0 - (minY + maxY) / 2.0);

            Stroke stroke = g2.getStroke();
            for (DrawCommand cmd : cmds) {
                ElementRenderer.drawElement(g2, cmd, stroke);
            }
            g2.dispose();
        }
    }

    /**
     * Row of clickable slider dots. The active slide's dot is filled in a
     * strong blue; clicking any dot navigates directly to that slide.
     */
    private class SlideDots extends JComponent {
        private static final int DOT = 16;
        private static final int GAP = 18;
        private static final Color ACTIVE   = new Color(40, 110, 230);
        private static final Color INACTIVE = new Color(178, 198, 232);

        private final int count;
        private int active = 0;

        SlideDots(int count) {
            this.count = count;
            int w = count * DOT + (count - 1) * GAP;
            setPreferredSize(new Dimension(w + 4, DOT + 12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int idx = dotIndexAt(e.getX());
                    if (idx >= 0) mostrarSlide(idx);
                }
            });
        }

        void setActive(int idx) {
            this.active = idx;
            repaint();
        }

        private int startX() {
            int w = count * DOT + (count - 1) * GAP;
            return (getWidth() - w) / 2;
        }

        private int dotIndexAt(int mouseX) {
            int x = startX();
            for (int i = 0; i < count; i++) {
                if (mouseX >= x && mouseX <= x + DOT) return i;
                x += DOT + GAP;
            }
            return -1;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int x = startX();
            int y = (getHeight() - DOT) / 2;
            for (int i = 0; i < count; i++) {
                g2.setColor(i == active ? ACTIVE : INACTIVE);
                g2.fillOval(x, y, DOT, DOT);
                x += DOT + GAP;
            }
            g2.dispose();
        }
    }
}
