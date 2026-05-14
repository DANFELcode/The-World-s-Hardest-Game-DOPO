package domain;

import java.awt.geom.Point2D;

/**
 * Represents a patrol movement strategy. The enemy follows a fixed route between waypoints. <br>
 * <b>(route, currentTargetIndex)</b> <br>
 * <b>Inv:</b> route != null and route.length > 0
 */
public class PatrolMovement implements MovementStrategy {
    
    private Point2D.Double[] route;
    private int currentTargetIndex;
    private final double speedInUnits;

    private PatrolMovement(Point2D.Double[] route, double speedInUnits) {
        if (route == null || route.length == 0) {
            throw new IllegalArgumentException("The patrol route must have at least one waypoint.");
        }
        this.route = route;
        this.currentTargetIndex = 0;
        this.speedInUnits = speedInUnits;
    }

    /** Basic patrol movement — 1x velocity. */
    public static PatrolMovement basic(Point2D.Double[] route) {
        return new PatrolMovement(route, 1.0*ENEMY_UNIT);
    }

    @Override
    public void move(Enemy enemy, Level level) {
        Point2D.Double target = route[currentTargetIndex];

        double dx = target.x - enemy.getX();
        double dy = target.y - enemy.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        double step = speedInUnits * MovableElement.UNIT;

        if (distance <= step) {
            enemy.setPosition(target.x, target.y);
            currentTargetIndex = (currentTargetIndex + 1) % route.length;
        } else {
            double dirX = dx / distance;
            double dirY = dy / distance;

            double newX = enemy.getX() + (dirX * step);
            double newY = enemy.getY() + (dirY * step);

            if (level.isWalkable(newX, newY, enemy.getWidth(), enemy.getHeight())) {
                enemy.setPosition(newX, newY);
            } else {
                currentTargetIndex = (currentTargetIndex + 1) % route.length;
            }
        }
    }

    @Override
    public String toFileParams() {
        StringBuilder sb = new StringBuilder("movement=patrol,route=");
        for (int i = 0; i < route.length; i++) {
            sb.append(route[i].x).append(":").append(route[i].y);
            if (i < route.length - 1) sb.append("|");
        }
        return sb.toString();
    }
}