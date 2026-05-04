package presentation;

import domain.*;
import java.awt.*;
import javax.swing.*;

/**
 * Renders the current level: zones, walls, coins, enemies and players. <br>
 * <b>(playerImg, enemyImg, coinImg, level)</b> <br>
 * <b>Inv:</b> true
 */
public class BoardPanel extends JPanel {

    private ImageIcon playerImg;
    private ImageIcon enemyImg;
    private ImageIcon coinImg;
    private Level level;

    /**
     * Creates the board panel with the default size and loads the asset images.
     */
    public BoardPanel() {
        playerImg = new ImageIcon("resources/imgJugadorRojo.png");
        enemyImg = new ImageIcon("resources/imgEnemigo.png");
        coinImg = new ImageIcon("resources/imgMoneda.png");
        setBackground(new Color(170, 190, 220));
        setPreferredSize(new Dimension(800, 500));
        setFocusable(true);
    }

    /**
     * Sets the level to be rendered.
     * @param level the level to render
     */
    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (level == null) return;

        // Zones (initial = light green, final = dark green, others = light yellow)
        for (java.util.Map.Entry<String, Zone> entry : level.getZones().entrySet()) {
            String type = entry.getKey();
            Zone zone = entry.getValue();
            if ("initial".equals(type))      g.setColor(new Color(170, 240, 170));
            else if ("final".equals(type))   g.setColor(new Color(60, 160, 60));
            else                              g.setColor(new Color(255, 240, 150));
            g.fillRect((int) zone.getX(), (int) zone.getY(),
                (int) zone.getWidth(), (int) zone.getHeight());
        }

        // Walls
        for (StaticElement e : level.getStaticElements()) {
            if (e.isBlocking()) {
                g.setColor(Color.BLACK);
                g.fillRect((int) e.getX(), (int) e.getY(),
                    (int) e.getWidth(), (int) e.getHeight());
            }
        }

        // Coins (SkinCoin in blue, regular Coin in yellow)
        for (Coin coin : level.getCoins()) {
            if (coin.isCollected()) continue;
            if (coin instanceof SkinCoin) {
                g.setColor(Color.BLUE);
            } else {
                g.setColor(new Color(218, 165, 32));
            }
            g.fillOval((int) coin.getX(), (int) coin.getY(),
                (int) coin.getWidth(), (int) coin.getHeight());
        }

        // Enemies as dark blue circles
        g.setColor(new Color(40, 60, 200));
        for (Enemy enemy : level.getEnemies()) {
            g.fillOval((int) enemy.getX(), (int) enemy.getY(),
                (int) enemy.getWidth(), (int) enemy.getHeight());
        }

        // Players as a red square with a black border
        for (Player player : level.getPlayers()) {
            g.setColor(Color.RED);
            g.fillRect((int) player.getX(), (int) player.getY(),
                (int) player.getWidth(), (int) player.getHeight());
            g.setColor(Color.BLACK);
            g.drawRect((int) player.getX(), (int) player.getY(),
                (int) player.getWidth(), (int) player.getHeight());
        }
    }

    /**
     * Triggers a repaint of the panel.
     */
    public void refresh() {
        repaint();
    }
}
