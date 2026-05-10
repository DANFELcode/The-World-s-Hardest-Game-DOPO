package presentation;

import domain.*;
import java.awt.*;
import javax.swing.JPanel;

public class BoardPanel extends JPanel {

    private static final Color COLOR_BACKGROUND    = new Color(170, 190, 220);
    private static final Color COLOR_PLAYER_BORDER = Color.BLACK;

    private Level level;

    public BoardPanel() {
        setBackground(COLOR_BACKGROUND);
        setPreferredSize(new Dimension(800, 500));
        setFocusable(true);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (level == null) return;

        for (Zone zone : level.getZones().values()) {
            g.setColor(zone.getDisplayColor());
            g.fillRect((int) zone.getX(), (int) zone.getY(),
                (int) zone.getWidth(), (int) zone.getHeight());
        }

        for (StaticElement e : level.getStaticElements()) {
            if (e.isBlocking()) {
                g.setColor(e.getDisplayColor());
                g.fillRect((int) e.getX(), (int) e.getY(),
                    (int) e.getWidth(), (int) e.getHeight());
            }
        }

        for (Coin coin : level.getCoins()) {
            if (coin.isCollected()) continue;
            g.setColor(coin.getDisplayColor());
            g.fillOval((int) coin.getX(), (int) coin.getY(),
                (int) coin.getWidth(), (int) coin.getHeight());
        }

        for (Enemy enemy : level.getEnemies()) {
            g.setColor(enemy.getDisplayColor());
            g.fillOval((int) enemy.getX(), (int) enemy.getY(),
                (int) enemy.getWidth(), (int) enemy.getHeight());
        }

        for (Player player : level.getPlayers()) {
            g.setColor(player.getDisplayColor());
            g.fillRect((int) player.getX(), (int) player.getY(),
                (int) player.getWidth(), (int) player.getHeight());
            g.setColor(COLOR_PLAYER_BORDER);
            g.drawRect((int) player.getX(), (int) player.getY(),
                (int) player.getWidth(), (int) player.getHeight());
        }
    }

    public void refresh() {
        repaint();
    }
}
