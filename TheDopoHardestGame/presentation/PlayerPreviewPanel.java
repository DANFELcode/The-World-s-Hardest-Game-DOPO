package presentation;

import java.awt.*;
import javax.swing.JPanel;

/**
 * Mini-canvas that shows a live preview of the selected player(s)
 * before the game starts. Reflects skin color, size and border color.
 */
public class PlayerPreviewPanel extends JPanel {

    private static final int BASE_SIZE   = 60;
    private static final int BLUE_SIZE   = 90; // 1.5x
    private static final int BORDER_STROKE = 4;
    private static final int CORNER_RADIUS = 22;

    private String skin1  = "red";
    private Color  border1 = Color.BLACK;
    private String skin2  = "red";
    private Color  border2 = Color.WHITE;
    private boolean showTwo = false;
    private String label2 = "P2";

    public PlayerPreviewPanel() {
        setPreferredSize(new Dimension(220, 200));
        setOpaque(false);
    }

    public void setSkin1(String skin)    { this.skin1 = skin;   repaint(); }
    public void setBorder1(Color color)  { this.border1 = color; repaint(); }
    public void setSkin2(String skin)    { this.skin2 = skin;   repaint(); }
    public void setBorder2(Color color)  { this.border2 = color; repaint(); }
    public void setShowTwo(boolean show) { this.showTwo = show;  repaint(); }
    public void setLabel2(String label)  { this.label2 = label;  repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Soft glass gradient background (light lavender → slightly darker)
        GradientPaint glass = new GradientPaint(
                0, 0, new Color(245, 245, 255, 220),
                0, h, new Color(210, 215, 245, 200));
        g2.setPaint(glass);
        g2.fillRoundRect(0, 0, w, h, CORNER_RADIUS, CORNER_RADIUS);

        // Top highlight band (glass sheen)
        GradientPaint sheen = new GradientPaint(
                0, 0, new Color(255, 255, 255, 140),
                0, h / 3, new Color(255, 255, 255, 0));
        g2.setPaint(sheen);
        g2.fillRoundRect(2, 2, w - 4, h / 2, CORNER_RADIUS, CORNER_RADIUS);

        // Soft border
        g2.setColor(new Color(150, 155, 215, 160));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, CORNER_RADIUS, CORNER_RADIUS);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2 + 10;

        if (showTwo) {
            int size1 = sizeFor(skin1);
            int size2 = sizeFor(skin2);
            int gap = 20;
            int leftX = cx - (size1 + gap + size2) / 2;
            drawPlayer(g2, leftX, cy - size1 / 2, size1, colorFor(skin1), border1, "P1");
            drawPlayer(g2, leftX + size1 + gap, cy - size2 / 2, size2, colorFor(skin2), border2, label2);
        } else {
            int size = sizeFor(skin1);
            drawPlayer(g2, cx - size / 2, cy - size / 2, size, colorFor(skin1), border1, "");
        }
    }

    private void drawPlayer(Graphics2D g2, int x, int y, int size, Color fill, Color border, String label) {
        // Fill
        g2.setColor(fill);
        g2.fillRect(x, y, size, size);

        // Border
        g2.setColor(border);
        g2.setStroke(new BasicStroke(BORDER_STROKE));
        g2.drawRect(x + 1, y + 1, size - 2, size - 2);

        // Label
        if (!label.isEmpty()) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            int lx = x + (size - fm.stringWidth(label)) / 2;
            int ly = y + size + 16;
            g2.setColor(new Color(40, 40, 40));
            g2.drawString(label, lx, ly);
        }
    }

    private Color colorFor(String skin) {
        switch (skin) {
            case "blue":  return Color.BLUE;
            case "green": return new Color(0, 150, 0);
            default:      return Color.RED;
        }
    }

    private int sizeFor(String skin) {
        return "blue".equals(skin) ? BLUE_SIZE : BASE_SIZE;
    }
}
