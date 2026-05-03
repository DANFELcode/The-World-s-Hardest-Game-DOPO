package test;

import org.junit.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import domain.Coin;
import domain.RedPlayer;

// Tests for the Coin domain class, verifying collection behaviour and state changes.
public class CoinTest {

    private RedPlayer player;

    // Initialises a RedPlayer instance before each test.
    @Before
    public void setUp() {
        player = new RedPlayer("Felipe", 100, 100);
    }

    
    // Verifies that calling onCollect marks the coin as collected.
    @Test
    public void onCollectShouldAddCoinToPlayer() {
        Coin coin = new Coin(10, 10, 15, 15, "Yellow");
        coin.onCollect(player);
        assertTrue(coin.isCollected());
    }

    
    // Verifies that a coin starts uncollected and becomes collected after onCollect is called.
    @Test
    public void onCollectShouldMarkCoinAsCollected() {
        Coin coin = new Coin(10, 10, 15, 15, "Yellow");
        assertFalse(coin.isCollected());
        coin.onCollect(player);
        assertTrue(coin.isCollected());
    }

   
    // Verifies that a newly created coin is not yet collected.
    @Test
    public void newCoinShouldNotBeCollected() {
        Coin coin = new Coin(10, 10, 15, 15, "Yellow");
        assertFalse(coin.isCollected());
    }
}