package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Enemy;
import domain.LinearMovement.Direction;
import domain.GameMap;
import domain.Level;
import domain.LinearMovement;
import domain.SolidWall;

// Tests for the LinearMovement strategy, verifying directional movement and wall bouncing.
class LinearMovementTest {

    private Level level;
    private LinearMovement horizontal;
    private LinearMovement vertical;

    // Initialises a Level and both movement strategies before each test.
    @BeforeEach
    void setUp() {
        GameMap map = new GameMap(500, 500);
        level = new Level(1, 60.0, map);
        horizontal = LinearMovement.basic(Direction.HORIZONTAL, 1);
        vertical = LinearMovement.basic(Direction.VERTICAL, 1);
    }

    @Test
    // Enemy should move right when no wall blocks the path.
    void moveShouldMoveHorizontallyWhenNoWall() {
        Enemy enemy = new Enemy(100, 100, 10, 10, horizontal);
        horizontal.move(enemy, level);
        assertEquals(104.0, enemy.getX(), 0.001);
        assertEquals(100.0, enemy.getY(), 0.001);
    }

    @Test
    // Enemy should reverse direction when hitting a horizontal wall.
    void moveShouldReverseDirectionWhenHitsWall() {
        SolidWall wall = new SolidWall(101, 100, 50, 50, "Gray");
        level.addStaticElement(wall);
        Enemy enemy = new Enemy(100, 100, 10, 10, horizontal);
        horizontal.move(enemy, level);
        horizontal.move(enemy, level);
        assertTrue(enemy.getX() <= 100.0);
    }

    @Test
    // Enemy should move down when no wall blocks the path.
    void moveShouldMoveVerticallyWhenNoWall() {
        Enemy enemy = new Enemy(100, 100, 10, 10, vertical);
        vertical.move(enemy, level);
        assertEquals(100.0, enemy.getX(), 0.001);
        assertEquals(104.0, enemy.getY(), 0.001);
    }

    @Test
    // Enemy should reverse vertical direction when hitting a wall.
    void moveShouldReverseVerticalDirectionWhenHitsWall() {
        SolidWall wall = new SolidWall(100, 101, 50, 50, "Gray");
        level.addStaticElement(wall);
        Enemy enemy = new Enemy(100, 100, 10, 10, vertical);
        vertical.move(enemy, level);
        vertical.move(enemy, level);
        assertTrue(enemy.getY() <= 100.0);
    }

    @Test
    // Accelerated factory moves at twice the basic step (×2 UNIT).
    void acceleratedShouldMoveTwiceFaster() {
        LinearMovement fast = LinearMovement.accelerated(Direction.HORIZONTAL, 1);
        Enemy enemy = new Enemy(100, 100, 10, 10, fast);
        fast.move(enemy, level);
        assertEquals(108.0, enemy.getX(), 0.001);
    }
}
