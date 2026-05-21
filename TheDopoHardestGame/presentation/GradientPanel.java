package presentation;

import java.awt.*;
import javax.swing.JPanel;

/**
 * Panel painted with a soft top-to-bottom gradient background.
 * Used as the base for the menu/selection panels.
 */
public class GradientPanel extends JPanel {

    /**
     * @param layout the layout manager for this panel
     */
    public GradientPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        GradientPaint bg = new GradientPaint(
            0, 0, new Color(213, 213, 255),
            0, getHeight(), Color.WHITE);
        g2.setPaint(bg);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
