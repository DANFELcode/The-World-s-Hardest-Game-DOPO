package domain;

/**
 * Represents a linear movement strategy. Moves the enemy in a straight line and bounces off walls. <br>
 * <b>(direction, sign)</b> <br>
 * <b>Inv:</b> sign == 1 or sign == -1
 */
public class LinearMovement implements MovementStrategy {

    private Direction direction; //horizontal o vertical el enemigo si tiene dirección fija
    private int sign; //1 o -1
    private final double speedInUnits; //1.0 o 2.0 para acelerado
    
    private LinearMovement(Direction direction, int sign, double speedInUnits) {
        this.direction = direction;
        this.sign = sign;
        this.speedInUnits = speedInUnits;
    }

    //factory methods(remplazan al constructor)
    //estaticos porque no necesitamos una isntancia previa para crearlos
    /** Basic linear movement — 1x velocity. */
    public static LinearMovement basic(Direction direction, int sign) {
        return new LinearMovement(direction, sign, 1.0*ENEMY_UNIT);
    }

    /** Accelerated linear movement — 2x velocity (Tipo A). */
    public static LinearMovement accelerated(Direction direction, int sign) {
        return new LinearMovement(direction, sign, 2.0*ENEMY_UNIT);
    }

    /**
     * Moves the enemy along its axis one tick. Reverses direction if a wall is found.
     * @param enemy the enemy to move
     * @param level the current level
     */
    @Override
    public void move(Enemy enemy, Level level) {
        double currentX = enemy.getX();
        double currentY = enemy.getY();
        double delta = sign * speedInUnits * MovableElement.UNIT; //si sign = 1 y el enemigo va horizontal se mueve a la derecha
        //si sign = 1 y el enemigo va vertical se mueve hacia arriba

        switch (direction) {
            case VERTICAL:
                double newY = currentY + delta;
                if (level.isWalkable(currentX, newY, enemy.getWidth(), enemy.getHeight())) {
                    enemy.setPosition(currentX, newY);
                } else {
                    sign *= -1; //si no es caminable quiere decir que hay una pared o esta fuera de los limites
                }
                break;
            case HORIZONTAL:
                double newX = currentX + delta;
                if (level.isWalkable(newX, currentY, enemy.getWidth(), enemy.getHeight())) {
                    enemy.setPosition(newX, currentY);
                } else {
                    sign *= -1;
                }
                break;
        }
    }

    @Override
    public String toFileParams() {
        String type = speedInUnits > 1.0 ? "accelerated" : "basic";
        return "movement=" + type + ",direction=" + direction + ",sign=" + sign;
    }
}
