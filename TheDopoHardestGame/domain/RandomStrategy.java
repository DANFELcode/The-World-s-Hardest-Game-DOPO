package domain;

import java.util.Random;

/**
 * Random walk strategy: the machine picks a direction at random and moves accordingly.
 * Validates moves via Level.isWalkable to avoid walls. <br>
 * <b>Inv:</b> dx in {-1, 0, 1} and dy in {-1, 0, 1}
 */
public class RandomStrategy implements GameStrategy {

    /** Percent chance per tick of switching direction randomly. */
    private static final int DIRECTION_CHANGE_CHANCE = 3;

    /** Cardinal + diagonal directions. Pure (0,0) excluded. */
    private static final int[][] DIRECTIONS = {
        { 0, -1}, { 0,  1}, {-1,  0}, { 1,  0},
        {-1, -1}, {-1,  1}, { 1, -1}, { 1,  1}
    };

    private final Random random;
    private int dx;
    private int dy;

    public RandomStrategy() {
        this.random = new Random();
        pickNewDirection();
    }

    /**
     * One step of random movement. If a wall blocks the move, a new direction
     * is picked so the player doesn't get stuck.
     */
    @Override
    public void execute(Player player, Level level) {
        if (random.nextInt(100) < DIRECTION_CHANGE_CHANCE) {
            pickNewDirection();
        }

        double prevX = player.getX();
        double prevY = player.getY();
        player.move(dx, dy, level);

        if (player.getX() == prevX && player.getY() == prevY) {
            pickNewDirection();
        }
    }

    private void pickNewDirection() {
        int[] dir = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        dx = dir[0];
        dy = dir[1];
    }
}
