package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Direction;
import domain.Enemy;
import domain.GameMap;
import domain.Level;
import domain.LinearMovement;
import domain.MovementStrategy;
import domain.RedPlayer;

// Tests for the Enemy domain class, covering destruction effects and movement strategy delegation.
class EnemyTest {

    private Level level;
    private RedPlayer player;

    // Initialises a Level and a RedPlayer instance before each test.
    @BeforeEach
    void setUp() {
        GameMap map = new GameMap(500, 500);
        level = new Level(1, 60.0, map);
        player = new RedPlayer("Felipe", 100, 100);
    }

    @Test
    // Verifies that onDestroy increments the player's death counter by one.
    void onDestroyShouldIncrementPlayerDeaths() {
        Enemy enemy = new Enemy(0, 0, 10, 10, 1.0,
                new LinearMovement(Direction.HORIZONTAL, 1));
        enemy.onDestroy(player);
        assertEquals(1, player.getDeaths());
    }

    @Test
    // Verifies that onDestroy respawns the player at their initial spawn position.
    void onDestroyShouldRespawnPlayer() {
        player.setPosition(250, 300);
        Enemy enemy = new Enemy(0, 0, 10, 10, 1.0,
                new LinearMovement(Direction.HORIZONTAL, 1));
        enemy.onDestroy(player);
        assertEquals(100.0, player.getX(), 0.001);
        assertEquals(100.0, player.getY(), 0.001);
    }

    @Test
    // Verifies that enemy.move delegates execution to the assigned movement strategy.
    void moveShouldDelegateToStrategy() {
        boolean[] called = {false};
        MovementStrategy strategy = (e, l) -> called[0] = true;
        Enemy enemy = new Enemy(0, 0, 10, 10, 1.0, strategy);
        enemy.move(level);
        assertTrue(called[0]);
    }
}
