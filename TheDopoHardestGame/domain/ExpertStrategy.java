package domain;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Hybrid expert AI: A* pathfinding for global navigation around walls,
 * combined with steering-behavior repulsion for local enemy avoidance.
 * A* targets the nearest uncollected coin owned by the player, or the
 * player's final zone if all coins are collected. <br>
 * <b>Inv:</b> CELL_SIZE > 0 and DANGER_RADIUS_SQ > 0
 */
public class ExpertStrategy implements GameStrategy {

    /** A* grid resolution in pixels. Smaller = finer paths, more CPU. */
    private static final double CELL_SIZE = 12.0;

    /** Squared radius (in pixels) at which an enemy starts producing repulsion. */
    private static final double DANGER_RADIUS_SQ = 60.0 * 60.0;

    /** Distance to a waypoint at which it is considered reached. */
    private static final double WAYPOINT_REACHED = 12.0;

    /** Ticks between A* recomputations when the target does not change. */
    private static final int PATH_REFRESH_TICKS = 15;

    /** Safety cap on A* search to avoid lag on unreachable targets. */
    private static final int A_STAR_MAX_ITERATIONS = 8000;

    /** Inflation margin used when testing collisions during A*. */
    private static final double COLLISION_MARGIN = 1.0;

    /** Strength multiplier applied to the enemy repulsion vector. */
    private static final double REPULSION_STRENGTH = 2.5;

    /** Cardinal + diagonal directions for A* neighbors. */
    private static final int[][] DIRECTIONS = {
        { 0,  1}, { 1,  0}, { 0, -1}, {-1,  0},
        { 1,  1}, { 1, -1}, {-1,  1}, {-1, -1}
    };

    private List<Point2D.Double> currentPath;
    private Interactable currentTarget;
    private int pathUpdateCounter;

    /**
     * Picks a target, refreshes the cached A* path if needed and moves the
     * player following the path while adding an enemy-repulsion vector.
     */
    @Override
    public void execute(Player player, Level level) {
        Interactable target = findBestTarget(player, level);
        if (target == null) return;

        if (currentPath == null || currentTarget != target
                || pathUpdateCounter++ > PATH_REFRESH_TICKS) {
            currentPath = calculateAStar(player, target, level);
            currentTarget = target;
            pathUpdateCounter = 0;
        }

        double dx = 0;
        double dy = 0;

        if (currentPath != null && !currentPath.isEmpty()) {
            Point2D.Double next = currentPath.get(0);
            if (Math.hypot(next.x - player.getX(), next.y - player.getY()) < WAYPOINT_REACHED) {
                currentPath.remove(0);
                if (!currentPath.isEmpty()) next = currentPath.get(0);
            }
            dx = next.x - player.getX();
            dy = next.y - player.getY();
            double dist = Math.hypot(dx, dy);
            if (dist > 0) {
                dx /= dist;
                dy /= dist;
            }
        }

        // Steering: enemies push the player away with strength inversely proportional to distance.
        if (!level.isInSafeZone(player)) {
            double repulseX = 0;
            double repulseY = 0;
            boolean avoiding = false;
            double dangerRadius = Math.sqrt(DANGER_RADIUS_SQ);
            for (Enemy enemy : level.getEnemies()) {
                if (enemy.isDead()) continue;
                double ex = enemy.getX() - player.getX();
                double ey = enemy.getY() - player.getY();
                double distSq = ex * ex + ey * ey;
                if (distSq >= DANGER_RADIUS_SQ) continue;
                double dist = Math.sqrt(distSq);
                if (dist < 0.1) dist = 0.1;
                double strength = (dangerRadius - dist) / dangerRadius;
                repulseX -= (ex / dist) * strength * REPULSION_STRENGTH;
                repulseY -= (ey / dist) * strength * REPULSION_STRENGTH;
                avoiding = true;
            }
            if (avoiding) {
                dx += repulseX;
                dy += repulseY;
            }
        }

        double finalDist = Math.hypot(dx, dy);
        if (finalDist > 0) {
            dx /= finalDist;
            dy /= finalDist;
        }

        if (tryMove(player, level, dx, dy)) return;
        if (dx != 0 && tryMove(player, level, dx,  0)) return;
        if (dy != 0 && tryMove(player, level,  0, dy)) return;
    }

    /** Attempts to move the player in (dx,dy) and returns whether the position actually changed. */
    private boolean tryMove(Player player, Level level, double dx, double dy) {
        if (dx == 0 && dy == 0) return false;
        double prevX = player.getX();
        double prevY = player.getY();
        player.move(dx, dy, level);
        return player.getX() != prevX || player.getY() != prevY;
    }

    /**
     * Returns the nearest uncollected coin owned by the player, or the
     * player's final zone if every owned coin has been collected.
     * @return the chosen target, or null if none is available
     */
    private Interactable findBestTarget(Player player, Level level) {
        Coin nearest = null;
        double minDist = Double.MAX_VALUE;
        String name = player.getName();
        for (Coin coin : level.getCoins()) {
            if (!name.equals(coin.getOwnerName()) || coin.isCollected()) continue;
            double dx = coin.getX() - player.getX();
            double dy = coin.getY() - player.getY();
            double dist = dx * dx + dy * dy;
            if (dist < minDist) {
                minDist = dist;
                nearest = coin;
            }
        }
        if (nearest != null) return nearest;
        return level.getZones().get("final_" + name);
    }

    //  A* 

    /** Internal A* search node: grid coordinates, scores and parent link. */
    private static class Node implements Comparable<Node> {
        int cx, cy;
        double f, g, h;
        Node parent;

        Node(int cx, int cy) {
            this.cx = cx;
            this.cy = cy;
        }

        @Override
        public int compareTo(Node o) {
            return Double.compare(this.f, o.f);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != Node.class) return false;
            Node o = (Node) obj;
            return this.cx == o.cx && this.cy == o.cy;
        }

        @Override
        public int hashCode() {
            return Objects.hash(cx, cy);
        }
    }

    /**
     * Runs A* on the cell grid from the player's current cell to the cell
     * containing the target's collision-area center. If the goal is not
     * reachable within the iteration cap, returns the path to the best-so-far
     * node (closest in heuristic distance to the goal).
     */
    private List<Point2D.Double> calculateAStar(Player player, Interactable target, Level level) {
        int startCx = (int) (player.getX() / CELL_SIZE);
        int startCy = (int) (player.getY() / CELL_SIZE);

        // Center of the target's collision area; works for both coins and zones.
        Rectangle2D area = target.getAreaColision();
        double tx = area.getX() + area.getWidth()  / 2.0 - player.getWidth()  / 2.0;
        double ty = area.getY() + area.getHeight() / 2.0 - player.getHeight() / 2.0;
        int targetCx = (int) (tx / CELL_SIZE);
        int targetCy = (int) (ty / CELL_SIZE);

        PriorityQueue<Node> openList = new PriorityQueue<>();
        Map<String, Node> allNodes = new HashMap<>();
        Set<String> closedList = new HashSet<>();

        Node start = new Node(startCx, startCy);
        start.g = 0;
        start.h = heuristic(startCx, startCy, targetCx, targetCy);
        start.f = start.g + start.h;
        openList.add(start);
        allNodes.put(startCx + "," + startCy, start);

        Node best = start;
        double bestDist = start.h;
        int iterations = 0;

        while (!openList.isEmpty() && iterations++ < A_STAR_MAX_ITERATIONS) {
            Node current = openList.poll();
            if (current.cx == targetCx && current.cy == targetCy) {
                return reconstructPath(current);
            }
            if (current.h < bestDist) {
                bestDist = current.h;
                best = current;
            }
            closedList.add(current.cx + "," + current.cy);

            for (int[] dir : DIRECTIONS) {
                int nx = current.cx + dir[0];
                int ny = current.cy + dir[1];
                String key = nx + "," + ny;
                if (closedList.contains(key)) continue;

                double realX = nx * CELL_SIZE;
                double realY = ny * CELL_SIZE;
                if (hasCollision(realX, realY, player, level)) continue;

                double moveCost = (dir[0] != 0 && dir[1] != 0) ? 1.414 : 1.0;
                double tentativeG = current.g + moveCost;

                Node neighbor = allNodes.get(key);
                if (neighbor == null) {
                    neighbor = new Node(nx, ny);
                    allNodes.put(key, neighbor);
                } else if (tentativeG >= neighbor.g) {
                    continue;
                }

                neighbor.parent = current;
                neighbor.g = tentativeG;
                neighbor.h = heuristic(nx, ny, targetCx, targetCy);
                neighbor.f = neighbor.g + neighbor.h;

                if (!openList.contains(neighbor)) {
                    openList.add(neighbor);
                } else {
                    openList.remove(neighbor);
                    openList.add(neighbor);
                }
            }
        }
        return reconstructPath(best);
    }

    /**
     * Returns true if a player-sized box (slightly inflated) at (realX, realY)
     * goes out of bounds, intersects a blocking wall, or intersects an active
     * hazard (e.g. a non-exploded bomb).
     */
    private boolean hasCollision(double realX, double realY, Player player, Level level) {
        if (realX < 0 || realY < 0
                || realX + player.getWidth()  > level.getMap().getWidth()
                || realY + player.getHeight() > level.getMap().getHeight()) {
            return true;
        }
        Rectangle2D.Double box = new Rectangle2D.Double(
            realX - COLLISION_MARGIN,
            realY - COLLISION_MARGIN,
            player.getWidth()  + COLLISION_MARGIN * 2,
            player.getHeight() + COLLISION_MARGIN * 2);
        for (StaticElement el : level.getStaticElements()) {
            if ((el.isBlocking() || el.isHazardous()) && box.intersects(el.getAreaColision())) {
                return true;
            }
        }
        return false;
    }

    /** Euclidean heuristic in grid units. */
    private double heuristic(int cx, int cy, int tx, int ty) {
        return Math.hypot(tx - cx, ty - cy);
    }

    /** Walks the parent chain from the goal node back to the start, in path order. */
    private List<Point2D.Double> reconstructPath(Node node) {
        List<Point2D.Double> path = new ArrayList<>();
        Node current = node;
        while (current != null) {
            path.add(0, new Point2D.Double(current.cx * CELL_SIZE, current.cy * CELL_SIZE));
            current = current.parent;
        }
        return path;
    }
}
