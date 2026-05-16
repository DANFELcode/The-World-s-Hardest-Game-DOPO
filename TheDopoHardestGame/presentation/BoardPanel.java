package presentation;

import domain.DrawCommand;
import domain.TheDOPOHardestGame;
import java.awt.*;
import javax.swing.JPanel;

public class BoardPanel extends JPanel {

    private static final Color COLOR_BACKGROUND = new Color(170, 190, 220);

    private TheDOPOHardestGame game;

    public BoardPanel() {
        setBackground(COLOR_BACKGROUND);
        setPreferredSize(new Dimension(800, 500));
        setFocusable(true);
    }

    public void setGame(TheDOPOHardestGame game) {
        this.game = game;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (game == null) return;

        Graphics2D g2 = (Graphics2D) g;
        Stroke originalStroke = g2.getStroke();
        for (DrawCommand cmd : game.getDrawCommands()) {
            g2.setColor(cmd.color);
            if (cmd.shape == DrawCommand.Shape.OVAL) {
                g2.fillOval(cmd.x, cmd.y, cmd.width, cmd.height);
            } else {
                g2.fillRect(cmd.x, cmd.y, cmd.width, cmd.height);
            }
            if (cmd.borderColor != null) {
                g2.setColor(cmd.borderColor);
                g2.setStroke(new BasicStroke(3));
                if (cmd.shape == DrawCommand.Shape.OVAL) {
                    g2.drawOval(cmd.x, cmd.y, cmd.width, cmd.height);
                } else {
                    g2.drawRect(cmd.x, cmd.y, cmd.width, cmd.height);
                }
                g2.setStroke(originalStroke);
            }
        }
    }

    public void refresh() {
        repaint();
    }
}
