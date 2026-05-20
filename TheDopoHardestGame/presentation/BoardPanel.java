package presentation;

import dto.DrawCommand;
import java.awt.*;
import javax.swing.JPanel;

public class BoardPanel extends JPanel {
    private java.util.List<DrawCommand> commands = new java.util.ArrayList<>();
    private Color backgroundColor = Color.WHITE;

    public BoardPanel() {
        setPreferredSize(new Dimension(800, 500));
        setFocusable(true);
    }
    
    public void updateGraphics(java.util.List<DrawCommand> commands, Color backgroundColor) {
        this.commands = commands;
        this.backgroundColor = backgroundColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (commands == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, getWidth(), getHeight());

        Stroke originalStroke = g2.getStroke();
        for (DrawCommand cmd : commands) {
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
            if (cmd.outerBorderColor != null) {
                g2.setColor(cmd.outerBorderColor);
                g2.setStroke(new BasicStroke(3));
                if (cmd.shape == DrawCommand.Shape.OVAL) {
                    g2.drawOval(cmd.x - 3, cmd.y - 3, cmd.width + 6, cmd.height + 6);
                } else {
                    g2.drawRect(cmd.x - 3, cmd.y - 3, cmd.width + 6, cmd.height + 6);
                }
                g2.setStroke(originalStroke);
            }
        }
    }

    public void refresh() {
        repaint();
    }
}
