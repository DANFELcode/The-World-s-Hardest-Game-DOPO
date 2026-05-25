package presentation;

import dto.DrawCommand;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import javax.swing.JPanel;

/**
 * Panel that renders the game board from a list of DrawCommands. <br>
 * Does not hold a reference to the domain — it only knows about DrawCommand (dto).
 */
public class BoardPanel extends JPanel {
    private static final int TARGET_CELL = 40; // desired checkerboard square size; actual size is fitted per level
    private static final int FLOOD_CELL = 5;   // resolution of the wall / flood-fill detection
    private static final Color GRID_COLOR_A = new Color(180, 181, 254); // matches default background
    private static final Color GRID_COLOR_B = new Color(205, 206, 255); // slightly lighter

    private List<DrawCommand> commands = new ArrayList<>();
    private Color backgroundColor = Color.WHITE;
    private final List<Effect> activeEffects = new ArrayList<>();
    private final Random rng = new Random();
    private boolean effectsEnabled = true;
    private boolean paused = false;

    /** Sets whether the paused overlay should be drawn over the board. */
    public void setPaused(boolean paused) { this.paused = paused; }

    public void setEffectsEnabled(boolean enabled) {
        this.effectsEnabled = enabled;
        if (!enabled) activeEffects.clear();
    }

    public boolean isEffectsEnabled() { return effectsEnabled; }

    private static final class Effect {
        final boolean isExplosion;
        final int cx, cy;
        int age;
        final int maxAge;
        final float[] particleAngles;
        final float[] particleSpeeds;

        Effect(boolean isExplosion, int cx, int cy, Random rng) {
            this.isExplosion = isExplosion;
            this.cx = cx;
            this.cy = cy;
            this.maxAge = isExplosion ? 40 : 20;
            int count = isExplosion ? 10 : 6;
            particleAngles = new float[count];
            particleSpeeds = new float[count];
            for (int i = 0; i < count; i++) {
                particleAngles[i] = (float)(Math.PI * 2 * i / count + rng.nextFloat() * 0.4f);
                particleSpeeds[i] = 0.6f + rng.nextFloat() * 0.8f;
            }
        }
    }

    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public BoardPanel() {
        setPreferredSize(new Dimension(800, 500));
        setFocusable(true);
    }

    /**
     * Updates the render data and schedules a repaint.
     * @param commands the list of draw commands for this frame
     * @param backgroundColor the board background color
     */
    public void updateGraphics(List<DrawCommand> commands, Color backgroundColor) {
        this.commands = commands;
        this.backgroundColor = backgroundColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (commands == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Center the level horizontally so it has equal left/right margins.
        int offsetX = horizontalCenterOffset();
        g2.translate(offsetX, 0);

        drawGrid(g2);

        Stroke originalStroke = g2.getStroke();
        for (DrawCommand cmd : commands) {
            ElementRenderer.drawElement(g2, cmd, originalStroke);
        }
        renderEffects(g2, originalStroke);

        g2.translate(-offsetX, 0);
        if (paused) renderPauseOverlay(g2);
    }

    /** Offset that horizontally centers the level's wall bounding box in the panel. */
    private int horizontalCenterOffset() {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        for (DrawCommand cmd : commands) {
            if (cmd.shape == DrawCommand.Shape.WALL) {
                minX = Math.min(minX, cmd.x);
                maxX = Math.max(maxX, cmd.x + cmd.width);
            }
        }
        if (minX == Integer.MAX_VALUE) return 0; // no walls: nothing to center
        return (getWidth() - (maxX - minX)) / 2 - minX;
    }

    /** Draws a translucent overlay with a pause icon and label centered on the board. */
    private void renderPauseOverlay(Graphics2D g2) {
        int w = getWidth(), h = getHeight();
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRect(0, 0, w, h);

        int cx = w / 2, cy = h / 2 - 14;
        int barW = 26, barH = 82, gap = 24, arc = 10;
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(cx - gap / 2 - barW, cy - barH / 2, barW, barH, arc, arc);
        g2.fillRoundRect(cx + gap / 2, cy - barH / 2, barW, barH, arc, arc);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        String txt = "PAUSA";
        g2.drawString(txt, cx - fm.stringWidth(txt) / 2, cy + barH / 2 + 46);
    }

    /** Adds a subtle hit-ring effect at the given center (enemy kill). */
    public void addEnemyHitEffect(int cx, int cy) {
        if (effectsEnabled) activeEffects.add(new Effect(false, cx, cy, rng));
    }

    /** Adds a full explosion effect at the given center (bomb kill). */
    public void addExplosionEffect(int cx, int cy) {
        if (effectsEnabled) activeEffects.add(new Effect(true, cx, cy, rng));
    }

    /** Advances all active effects by one tick and removes expired ones. */
    public void tickEffects() {
        for (Effect e : activeEffects) e.age++;
        activeEffects.removeIf(e -> e.age >= e.maxAge);
    }

    private void renderEffects(Graphics2D g2, Stroke originalStroke) {
        for (Effect e : activeEffects) {
            float t = (float) e.age / e.maxAge;
            int alpha = Math.max(0, (int)((1 - t) * 220));
            if (e.isExplosion) {
                renderExplosion(g2, e, t, alpha, originalStroke);
            } else {
                renderEnemyHit(g2, e, t, alpha, originalStroke);
            }
        }
    }

    private void renderEnemyHit(Graphics2D g2, Effect e, float t, int alpha, Stroke originalStroke) {
        int ringR = (int)(t * 28);
        g2.setColor(new Color(220, 50, 50, alpha));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawOval(e.cx - ringR, e.cy - ringR, ringR * 2, ringR * 2);
        g2.setStroke(originalStroke);

        for (int i = 0; i < e.particleAngles.length; i++) {
            float dist = e.particleSpeeds[i] * t * 32;
            int px = (int)(e.cx + Math.cos(e.particleAngles[i]) * dist);
            int py = (int)(e.cy + Math.sin(e.particleAngles[i]) * dist);
            int size = Math.max(2, (int)((1 - t) * 5));
            g2.setColor(new Color(230, 70, 50, alpha));
            g2.fillOval(px - size / 2, py - size / 2, size, size);
        }
    }

    private void renderExplosion(Graphics2D g2, Effect e, float t, int alpha, Stroke originalStroke) {
        // White flash in first 20% of lifetime
        if (t < 0.2f) {
            float ft = t / 0.2f;
            int flashAlpha = (int)((1 - ft) * 210);
            int flashR = (int)(ft * 35);
            g2.setColor(new Color(255, 255, 255, flashAlpha));
            g2.fillOval(e.cx - flashR, e.cy - flashR, flashR * 2, flashR * 2);
        }

        // Expanding orange-yellow fireball
        int outerR = (int)(t * 65);
        if (outerR > 0) {
            Paint saved = g2.getPaint();
            RadialGradientPaint fire = new RadialGradientPaint(
                new Point2D.Float(e.cx, e.cy), outerR,
                new float[]{0f, 0.6f, 1f},
                new Color[]{
                    new Color(255, 240, 100, alpha),
                    new Color(255, 120,  20, alpha),
                    new Color(180,  40,   0,       0)
                },
                MultipleGradientPaint.CycleMethod.NO_CYCLE);
            g2.setPaint(fire);
            g2.fillOval(e.cx - outerR, e.cy - outerR, outerR * 2, outerR * 2);
            g2.setPaint(saved);
        }

        // Radiating particles
        Color[] palette = {
            new Color(255, 220,  80),
            new Color(255, 140,  20),
            new Color(255,  60,   0),
            new Color(255, 255, 140)
        };
        for (int i = 0; i < e.particleAngles.length; i++) {
            float dist = e.particleSpeeds[i] * t * 75;
            int px = (int)(e.cx + Math.cos(e.particleAngles[i]) * dist);
            int py = (int)(e.cy + Math.sin(e.particleAngles[i]) * dist);
            int size = Math.max(2, (int)((1 - t) * 9));
            Color base = palette[i % palette.length];
            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha));
            g2.fillOval(px - size / 2, py - size / 2, size, size);
        }
    }

    /**
     * Draws a decorative checkerboard grid behind all game elements.
     * Wall detection and the enclosed-area flood fill run at FLOOD_CELL
     * resolution (fine), so the grid reaches close to the thin walls. The
     * checkerboard square size is fitted to the wall bounding box so a whole
     * number of squares aligns exactly with the play area edges. A flood fill
     * from the board edges marks every cell reachable from the outside, and
     * the grid fills only the remaining enclosed cells.
     */
    private void drawGrid(Graphics2D g2) {
        int cols = (int) Math.ceil((double) getWidth() / FLOOD_CELL);
        int rows = (int) Math.ceil((double) getHeight() / FLOOD_CELL);
        if (cols <= 0 || rows <= 0) return;

        // Mark fine cells touched by any wall, and track the wall bounding box.
        boolean[][] isWall = new boolean[rows][cols];
        boolean hasWalls = false;
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (DrawCommand cmd : commands) {
            if (cmd.shape == DrawCommand.Shape.WALL) {
                hasWalls = true;
                minX = Math.min(minX, cmd.x);
                minY = Math.min(minY, cmd.y);
                maxX = Math.max(maxX, cmd.x + cmd.width);
                maxY = Math.max(maxY, cmd.y + cmd.height);
                int c0 = Math.max(0, cmd.x / FLOOD_CELL);
                int r0 = Math.max(0, cmd.y / FLOOD_CELL);
                int c1 = Math.min(cols - 1, (cmd.x + cmd.width - 1) / FLOOD_CELL);
                int r1 = Math.min(rows - 1, (cmd.y + cmd.height - 1) / FLOOD_CELL);
                for (int r = r0; r <= r1; r++) {
                    for (int c = c0; c <= c1; c++) {
                        isWall[r][c] = true;
                    }
                }
            }
        }
        if (!hasWalls) return;

        // Flood fill the exterior starting from every non-wall board-edge cell.
        boolean[][] isExterior = new boolean[rows][cols];
        Deque<int[]> queue = new ArrayDeque<>();
        for (int c = 0; c < cols; c++) {
            seedExterior(0, c, isWall, isExterior, queue);
            seedExterior(rows - 1, c, isWall, isExterior, queue);
        }
        for (int r = 0; r < rows; r++) {
            seedExterior(r, 0, isWall, isExterior, queue);
            seedExterior(r, cols - 1, isWall, isExterior, queue);
        }
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            for (int[] d : dirs) {
                int nr = cell[0] + d[0];
                int nc = cell[1] + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && !isWall[nr][nc] && !isExterior[nr][nc]) {
                    isExterior[nr][nc] = true;
                    queue.add(new int[]{nr, nc});
                }
            }
        }

        // Fit a whole number of checker squares into the wall bounding box, so
        // the checkerboard aligns exactly with the play area edges (no cut
        // squares at the corners). Cell size ends up close to TARGET_CELL.
        int boundsW = maxX - minX;
        int boundsH = maxY - minY;
        int checkerCols = Math.max(1, (int) Math.round(boundsW / (double) TARGET_CELL));
        int checkerRows = Math.max(1, (int) Math.round(boundsH / (double) TARGET_CELL));
        double cellW = boundsW / (double) checkerCols;
        double cellH = boundsH / (double) checkerRows;

        // Draw enclosed (non-wall, non-exterior) fine cells, colored by the
        // fitted checkerboard pattern. Consecutive same-color cells in a row
        // are merged into a single fillRect to keep rendering cheap.
        for (int row = 0; row < rows; row++) {
            int checkerRow = (int) Math.floor((row * FLOOD_CELL - minY) / cellH);
            int runStart = -1;
            Color runColor = null;
            for (int col = 0; col <= cols; col++) {
                Color cellColor = null;
                if (col < cols && !isWall[row][col] && !isExterior[row][col]) {
                    int checkerCol = (int) Math.floor((col * FLOOD_CELL - minX) / cellW);
                    cellColor = Math.floorMod(checkerRow + checkerCol, 2) == 0 ? GRID_COLOR_A : GRID_COLOR_B;
                }
                if (cellColor != runColor) {
                    if (runColor != null) {
                        g2.setColor(runColor);
                        g2.fillRect(runStart * FLOOD_CELL, row * FLOOD_CELL,
                                (col - runStart) * FLOOD_CELL, FLOOD_CELL);
                    }
                    runStart = col;
                    runColor = cellColor;
                }
            }
        }
    }

    /** Marks a board-edge cell as exterior and enqueues it for flood fill. */
    private void seedExterior(int r, int c, boolean[][] isWall, boolean[][] isExterior, Deque<int[]> queue) {
        if (!isWall[r][c] && !isExterior[r][c]) {
            isExterior[r][c] = true;
            queue.add(new int[]{r, c});
        }
    }

    public void refresh() {
        repaint();
    }
}
