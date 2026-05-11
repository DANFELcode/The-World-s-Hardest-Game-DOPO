package domain;

/**
 * Represents a linear movement strategy. Moves the enemy in a straight line and bounces off walls. <br>
 * <b>(direction, sign)</b> <br>
 * <b>Inv:</b> sign == 1 or sign == -1
 */
public class LinearMovement implements MovementStrategy {

    public enum Direction { HORIZONTAL, VERTICAL }

    private Direction direction;
    private int sign;

    /**
     * Creates a linear movement strategy.
     * @param direction axis of movement (HORIZONTAL or VERTICAL)
     * @param sign direction of movement: 1 (forward) or -1 (backward)
     */
    public LinearMovement(Direction direction, int sign) {
        this.direction = direction;
        this.sign = sign;
    }

    /**
     * Moves the enemy along its axis. Reverses direction if a wall is found.
     * @param enemy the enemy to move
     * @param level the current level
     */
    public void move(Enemy enemy, Level level) {
        double currentX = enemy.getX();
        double currentY = enemy.getY();
        double delta = sign * enemy.getSpeed() * MovableElement.UNIT;

        switch (direction) {
            case VERTICAL:
                double newY = currentY + delta;
                if (level.isWalkable(currentX, newY, enemy.getWidth(), enemy.getHeight())) {
                    enemy.setPosition(currentX, newY);
                } else {
                    sign *= -1;
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
}