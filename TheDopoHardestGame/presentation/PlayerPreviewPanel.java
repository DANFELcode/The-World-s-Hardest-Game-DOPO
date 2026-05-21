package presentation;

import dto.DrawCommand;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 * Mini-canvas that shows a live preview of the selected player(s) over the
 * game board background, before the game starts. The player squares scale
 * automatically to fit the panel, so they never overflow the card.
 */
public class PlayerPreviewPanel extends JPanel {

    private static final int CORNER_RADIUS = 22;
    private static final Color PREVIEW_BG = new Color(236, 237, 250);

    private String skin1  = "red";
    private Color  border1 = Color.BLACK;
    private String skin2  = "red";
    private Color  border2 = Color.WHITE;
    private boolean showTwo = false;
    private String label2 = "P2";

    public PlayerPreviewPanel() {
        setPreferredSize(new Dimension(180, 190));
        setOpaque(false);
    }

    public void setSkin1(String skin)    { this.skin1 = skin;    repaint(); }
    public void setBorder1(Color color)  { this.border1 = color; repaint(); }
    public void setSkin2(String skin)    { this.skin2 = skin;    repaint(); }
    public void setBorder2(Color color)  { this.border2 = color; repaint(); }
    public void setShowTwo(boolean show) { this.showTwo = show;  repaint(); }
    public void setLabel2(String label)  { this.label2 = label;  repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth(), h = getHeight();

        // Soft light card that blends with the panel's lavender theme.
        Shape card = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.setColor(PREVIEW_BG);
        g2.fill(card);
        g2.setColor(new Color(170, 172, 215));
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(card);

        int pad = 22;
        int labelSpace = showTwo ? 28 : 0;
        double r1 = ratioFor(skin1);
        double r2 = ratioFor(skin2);
        Stroke stroke = g2.getStroke();

        if (showTwo) {
            int gap = 16;
            double maxR = Math.max(r1, r2);
            // Largest base size that keeps both squares (+ gap + labels) inside the card.
            double byWidth  = (w - 2 * pad - gap) / (r1 + r2);
            double byHeight = (h - 2 * pad - labelSpace) / maxR;
            double base = Math.min(Math.min(byWidth, byHeight), 64);
            int s1 = (int) (r1 * base);
            int s2 = (int) (r2 * base);
            int cx = w / 2;
            int cy = (h - labelSpace) / 2 + 4;
            int leftX = cx - (s1 + gap + s2) / 2;
            drawPlayer(g2, leftX, cy - s1 / 2, s1, colorFor(skin1), border1, "P1", stroke);
            drawPlayer(g2, leftX + s1 + gap, cy - s2 / 2, s2, colorFor(skin2), border2, label2, stroke);
        } else {
            double base = Math.min(Math.min((w - 2 * pad) / r1, (h - 2 * pad) / r1), 70);
            int s = (int) (r1 * base);
            drawPlayer(g2, w / 2 - s / 2, h / 2 - s / 2, s, colorFor(skin1), border1, "", stroke);
        }
    }

    private void drawPlayer(Graphics2D g2, int x, int y, int size,
                            Color fill, Color border, String label, Stroke stroke) {
        ElementRenderer.drawElement(g2,
            new DrawCommand(fill, x, y, size, size, DrawCommand.Shape.PLAYER, border), stroke);
        if (!label.isEmpty()) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            int lx = x + (size - fm.stringWidth(label)) / 2;
            int ly = y + size + 17;
            g2.setColor(new Color(25, 25, 35));
            g2.drawString(label, lx, ly);
        }
    }

    private Color colorFor(String skin) {
        switch (skin) {
            case "blue":  return new Color(55, 90, 220);
            case "green": return new Color(0, 150, 0);
            default:      return new Color(220, 55, 55);
        }
    }

    /** Size ratio relative to the base square: blue is 1.5x, the rest 1x. */
    private double ratioFor(String skin) {
        return "blue".equals(skin) ? 1.5 : 1.0;
    }
}
