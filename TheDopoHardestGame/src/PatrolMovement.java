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

    /**
     * Creates a patrol movement with a specific route.
     * @param route an array of waypoints the enemy will follow
     */
    public PatrolMovement(Point2D.Double[] route) {
        if (route == null || route.length == 0) {
            throw new IllegalArgumentException("The patrol route must have at least one waypoint.");
        }
        this.route = route;
        this.currentTargetIndex = 0;
    }

    @Override
    public void move(Enemy enemy, Level level) {
        Point2D.Double target = route[currentTargetIndex];
        
        double dx = target.x - enemy.getX();
        double dy = target.y - enemy.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // CORRECCIÓN: Usar la misma constante del compañero
        double step = enemy.getSpeed() * MovableElement.UNIT; 

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
}