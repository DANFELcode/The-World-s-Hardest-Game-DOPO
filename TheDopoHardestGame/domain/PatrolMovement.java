package domain;

import java.awt.geom.Point2D;

/**
 * Represents a patrol movement strategy. The enemy follows a fixed route between waypoints. <br>
 * <b>(route, currentTargetIndex)</b> <br>
 * <b>Inv:</b> route != null and route.length > 0
 */
public final class PatrolMovement implements MovementStrategy {
    
    private Point2D.Double[] route; //ruta con puntos de referencia
    private int currentTargetIndex; //indice del punto de referencia actual(al que se esta dirigiendo el enemigo)
    private final double speedInUnits; //1.0, no es estatico por extensibilidad

    //constructor privado debido a factory methods
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
        return new PatrolMovement(route, 1.0*ENEMY_UNIT); //velocidad ajustada a la unidad del enemigo
    }

    @Override
    public void move(Enemy enemy, Level level) {
        Point2D.Double target = route[currentTargetIndex]; //punto de referencia

        double dx = target.x - enemy.getX(); //desplazamiento x para llegar
        double dy = target.y - enemy.getY(); //desplazamiento y para llegar
        double distance = Math.sqrt(dx * dx + dy * dy);

        double step = speedInUnits * MovableElement.UNIT; //cuanto avanzara el enemigo, consistencia

        //si la distancia al target es mayor que step se mueve paso completo normalmente
        //si es menor o igual se teletransporta exactamente punto de referencia y avanza al siguiente
        if (distance <= step) {
            enemy.setPosition(target.x, target.y); //mueve al punto de referencia
            currentTargetIndex = (currentTargetIndex + 1) % route.length; //actualiza el siguiente punto de referencia
            //% route.length hace que cuando llegás al último, vuelva a 0.
        } else {
            double dirX = dx / distance; //normalizamos
            double dirY = dy / distance;

            //dirX y dirY forman un vector de longitud 1 que se multiplica por step
            double newX = enemy.getX() + (dirX * step);
            double newY = enemy.getY() + (dirY * step);

            if (level.isWalkable(newX, newY, enemy.getWidth(), enemy.getHeight())) {
                enemy.setPosition(newX, newY);
            } else {
                currentTargetIndex = (currentTargetIndex + 1) % route.length; //si no es caminable salta directamente al siguiente punto de referencia
            }
        }
    }

    @Override
    public String toFileParams() {
        StringBuilder sb = new StringBuilder("movement=patrol,route="); //clase de java para construir string de forma eficiente
        for (int i = 0; i < route.length; i++) {
            sb.append(route[i].x).append(":").append(route[i].y);
            if (i < route.length - 1) sb.append("|"); //separador para gameDataAcess
        }
        return sb.toString();
    }
}