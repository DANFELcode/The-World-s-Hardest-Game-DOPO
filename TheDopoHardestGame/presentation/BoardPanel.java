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

        for (DrawCommand cmd : game.getDrawCommands()) {
            g.setColor(cmd.color);
            if (cmd.shape == DrawCommand.Shape.OVAL) {
                g.fillOval(cmd.x, cmd.y, cmd.width, cmd.height);
            } else {
                g.fillRect(cmd.x, cmd.y, cmd.width, cmd.height);
            }
            if (cmd.borderColor != null) {
                g.setColor(cmd.borderColor);
                g.drawRect(cmd.x, cmd.y, cmd.width, cmd.height);
            }
        }
    }

    public void refresh() {
        repaint();
    }
}
