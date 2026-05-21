package presentation;

import dto.DrawCommand;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
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
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, getWidth(), getHeight());

        drawGrid(g2);

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
