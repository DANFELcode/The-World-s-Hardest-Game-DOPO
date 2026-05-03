package test;

import org.junit.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import domain.Coin;
import domain.RedPlayer;

public class CoinTest {

    private RedPlayer player;

    @Before
    public void setUp() {
        player = new RedPlayer("Felipe", 100, 100);
    }

    
    @Test
    public void onCollectShouldAddCoinToPlayer() {
        Coin coin = new Coin(10, 10, 15, 15, "Yellow");
        coin.onCollect(player);
        assertTrue(coin.isCollected());
    }

    
    @Test
    public void onCollectShouldMarkCoinAsCollected() {
        Coin coin = new Coin(10, 10, 15, 15, "Yellow");
        assertFalse(coin.isCollected());
        coin.onCollect(player);
        assertTrue(coin.isCollected());
    }

   
    @Test
    public void newCoinShouldNotBeCollected() {
        Coin coin = new Coin(10, 10, 15, 15, "Yellow");
        assertFalse(coin.isCollected());
    }
}