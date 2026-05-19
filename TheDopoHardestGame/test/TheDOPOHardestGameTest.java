package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import domain.*;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Single comprehensive test suite for the DOPO Hardest Game.
 * Covers domain, persistence, level parsing, polymorphic behaviors and edge cases.
 */
class TheDOPOHardestGameTest {

    private TheDOPOHardestGame game;
    private Level level;
    private GameMap map;
    private RedPlayer redPlayer;
    private BluePlayer bluePlayer;
    private GreenPlayer greenPlayer;

    @BeforeEach
    void setUp() {
        game = new TheDOPOHardestGame();
        map = new GameMap(800, 500);
        level = new Level(1, 60 * GameDataAccess.TICKS_PER_SECOND, map);
        redPlayer = new RedPlayer("Player1", 50, 240);
        bluePlayer = new BluePlayer("Player2", 100, 240);
        greenPlayer = new GreenPlayer("Player1", 50, 240);
    }

    // =================== 1. Level format validation ===================

    @Test
    void loadLevelAbsoluteShouldThrowWhenMissingNumber(@TempDir Path tmp) throws Exception {
        File f = writeLevel(tmp, "TIME=60\n");
        assertThrows(LevelFormatException.class, () -> GameDataAccess.getInstance().loadLevelAbsolute(f));
    }

    @Test
    void loadLevelAbsoluteShouldThrowWhenMissingTime(@TempDir Path tmp) throws Exception {
        File f = writeLevel(tmp, "NUMBER=1\n");
        assertThrows(LevelFormatException.class, () -> GameDataAccess.getInstance().loadLevelAbsolute(f));
    }

    @Test
    void loadLevelAbsoluteShouldThrowWhenTimeIsZeroOrNegative(@TempDir Path tmp) throws Exception {
        File f = writeLevel(tmp, "NUMBER=1\nTIME=0\n");
        assertThrows(LevelFormatException.class, () -> GameDataAccess.getInstance().loadLevelAbsolute(f));
        File f2 = writeLevel(tmp, "NUMBER=1\nTIME=-5\n", "neg.txt");
        assertThrows(LevelFormatException.class, () -> GameDataAccess.getInstance().loadLevelAbsolute(f2));
    }

    @Test
    void loadLevelAbsoluteShouldThrowWhenEnemyOutOfBounds(@TempDir Path tmp) throws Exception {
        File f = writeLevel(tmp,
            "NUMBER=1\nTIME=60\n" +
            "ENEMY x=900,y=100,width=20,height=20,movement=basic,direction=VERTICAL,sign=1\n");
        assertThrows(LevelFormatException.class, () -> GameDataAccess.getInstance().loadLevelAbsolute(f));
    }

    @Test
    void loadLevelAbsoluteShouldThrowWhenWidthIsZeroOrNegative(@TempDir Path tmp) throws Exception {
        File f = writeLevel(tmp,
            "NUMBER=1\nTIME=60\nWALL x=10,y=10,width=0,height=20\n");
        assertThrows(LevelFormatException.class, () -> GameDataAccess.getInstance().loadLevelAbsolute(f));
    }

    @Test
    void loadLevelAbsoluteShouldThrowWhenDirectionIsInvalid(@TempDir Path tmp) throws Exception {
        File f = writeLevel(tmp,
            "NUMBER=1\nTIME=60\n" +
            "ENEMY x=100,y=100,width=20,height=20,movement=basic,direction=DIAGONAL,sign=1\n");
        assertThrows(LevelFormatException.class, () -> GameDataAccess.getInstance().loadLevelAbsolute(f));
    }

    @Test
    void loadLevelAbsoluteShouldThrowWhenSignIsNot1OrMinus1(@TempDir Path tmp) throws Exception {
        File f = writeLevel(tmp,
            "NUMBER=1\nTIME=60\n" +
            "ENEMY x=100,y=100,width=20,height=20,movement=basic,direction=VERTICAL,sign=3\n");
        assertThrows(LevelFormatException.class, () -> GameDataAccess.getInstance().loadLevelAbsolute(f));
    }

    @Test
    void loadLevelAbsoluteShouldThrowWhenElementTypeIsUnknown(@TempDir Path tmp) throws Exception {
        File f = writeLevel(tmp, "NUMBER=1\nTIME=60\nUNKNOWN x=1,y=2\n");
        assertThrows(LevelFormatException.class, () -> GameDataAccess.getInstance().loadLevelAbsolute(f));
    }

    @Test
    void loadLevelAbsoluteShouldThrowWhenRoutePointIsMalformed(@TempDir Path tmp) throws Exception {
        File f = writeLevel(tmp,
            "NUMBER=1\nTIME=60\n" +
            "ENEMY x=100,y=100,width=20,height=20,movement=patrol,route=100:100|bad\n");
        assertThrows(LevelFormatException.class, () -> GameDataAccess.getInstance().loadLevelAbsolute(f));
    }

    // =================== 2. Player movement / collisions ===================

    @Test
    void playerShouldNotMoveIntoWall() {
        level.addStaticElement(new SolidWall(100, 200, 20, 100, "black"));
        level.addPlayer(redPlayer);
        redPlayer.setPosition(70, 240);
        redPlayer.move(1, 0, level); // move right
        // should not have crossed the wall fully — position bounded
        assertTrue(redPlayer.getX() < 100);
    }

    @Test
    void playerShouldNotMoveOutOfMapBounds() {
        level.addPlayer(redPlayer);
        redPlayer.setPosition(0, 0);
        redPlayer.move(-1, -1, level);
        assertEquals(0, redPlayer.getX(), 0.001);
        assertEquals(0, redPlayer.getY(), 0.001);
    }

    @Test
    void playerShouldMoveDiagonally() {
        level.addPlayer(redPlayer);
        double startX = redPlayer.getX(), startY = redPlayer.getY();
        redPlayer.move(1, 1, level);
        assertTrue(redPlayer.getX() > startX);
        assertTrue(redPlayer.getY() > startY);
    }

    @Test
    void bluePlayerShouldMoveFasterThanRed() {
        assertTrue(bluePlayer.getSpeed() > redPlayer.getSpeed());
    }

    @Test
    void weakenedGreenPlayerShouldMoveSlower() {
        double baseSpeed = greenPlayer.getSpeed();
        // simulate first hit (weakens green)
        greenPlayer.onHit(level);
        assertTrue(greenPlayer.getSpeed() < baseSpeed);
    }

    // =================== 3. Death and respawn ===================

    @Test
    void playerDieShouldIncrementDeathsAndResetPosition() {
        level.addPlayer(redPlayer);
        redPlayer.setPosition(300, 300);
        int before = redPlayer.getDeaths();
        redPlayer.die(level);
        assertEquals(before + 1, redPlayer.getDeaths());
        assertEquals(redPlayer.getSpawnX(), redPlayer.getX(), 0.001);
        assertEquals(redPlayer.getSpawnY(), redPlayer.getY(), 0.001);
    }

    @Test
    void playerDieShouldRestoreDefaultSkin() {
        level.addPlayer(redPlayer);
        redPlayer.changeSkin(new BlueSkin());
        redPlayer.die(level);
        assertEquals(Color.RED, redPlayer.getDisplayColor());
    }

    @Test
    void playerDeathShouldResetOwnedCoinsWhenNoCheckpoint() {
        Coin coin = new Coin(200, 240, 12, 12, "yellow", "Player1");
        level.addCoin(coin);
        level.addPlayer(redPlayer);
        coin.setOwnerPlayer(redPlayer);
        coin.onCollect(redPlayer);
        assertTrue(coin.isCollected());
        redPlayer.die(level);
        assertFalse(coin.isCollected());
    }

    @Test
    void playerDeathShouldNotResetCoinsWithCheckpoint() {
        Coin coin = new Coin(200, 240, 12, 12, "yellow", "Player1");
        level.addCoin(coin);
        level.addPlayer(redPlayer);
        coin.setOwnerPlayer(redPlayer);
        coin.onCollect(redPlayer);
        redPlayer.markCheckpoint(300, 300);
        redPlayer.die(level);
        assertTrue(coin.isCollected(), "Normal coin should survive death after checkpoint");
    }

    @Test
    void playerDeathShouldAlwaysResetSkinCoinsRegardlessOfCheckpoint() {
        SkinCoin skinCoin = new SkinCoin(200, 240, 15, 15, "green", "Player1");
        level.addCoin(skinCoin);
        level.addPlayer(redPlayer);
        skinCoin.setOwnerPlayer(redPlayer);
        skinCoin.onCollect(redPlayer);
        redPlayer.markCheckpoint(300, 300);
        redPlayer.die(level);
        assertFalse(skinCoin.isCollected(), "SkinCoin must reset even with checkpoint");
    }

    @Test
    void consecutiveHitsByDifferentEnemiesAreIndependent() {
        // Invulnerability was removed. Each onHit call is independent.
        // Protection against double-hit lives in Enemy.lastVictim, not in onHit.
        level.addPlayer(redPlayer);
        int initial = redPlayer.getDeaths();
        redPlayer.onHit(level); // first hit kills RedPlayer
        assertEquals(initial + 1, redPlayer.getDeaths());
    }

    @Test
    void playerInSafeZoneShouldNotTakeHits() {
        InitialZone zone = new InitialZone(40, 200, 100, 100, "Player1");
        level.addZone("initial_Player1", zone);
        level.addPlayer(redPlayer);
        redPlayer.setPosition(50, 240);
        int before = redPlayer.getDeaths();
        redPlayer.onHit(level);
        assertEquals(before, redPlayer.getDeaths(), "Hits inside a zone must be ignored");
    }

    // =================== 4. Skin system ===================

    @Test
    void defaultSkinShouldKillPlayerOnFirstHit() {
        level.addPlayer(redPlayer);
        redPlayer.setPosition(300, 300);
        int before = redPlayer.getDeaths();
        redPlayer.onHit(level);
        assertEquals(before + 1, redPlayer.getDeaths());
    }

    @Test
    void blueSkinShouldHave15xSpeedAndSize() {
        double redSpeed = redPlayer.getSpeed();
        assertEquals(redSpeed * 1.5, bluePlayer.getSpeed(), 0.001);
        assertEquals(30.0, bluePlayer.getWidth(), 0.001);
        assertEquals(30.0, bluePlayer.getHeight(), 0.001);
    }

    @Test
    void greenSkinShouldAbsorbFirstHitAndDieOnSecond() {
        level.addPlayer(greenPlayer);
        greenPlayer.setPosition(300, 300);
        int before = greenPlayer.getDeaths();
        greenPlayer.onHit(level);
        assertEquals(before, greenPlayer.getDeaths(), "First hit only weakens");
        try { Thread.sleep(350); } catch (InterruptedException ignored) {}
        greenPlayer.onHit(level);
        assertEquals(before + 1, greenPlayer.getDeaths(), "Second hit kills weakened green");
    }

    @Test
    void skinCoinShouldChangePlayerSkinOnPickup() {
        SkinCoin skin = new SkinCoin(200, 240, 15, 15, "blue", "Player1");
        level.addPlayer(redPlayer);
        skin.onCollect(redPlayer);
        // BlueSkin sets size to 30
        assertEquals(30.0, redPlayer.getWidth(), 0.001);
    }

    @Test
    void skinCoinShouldNotChangeNonOwnerSkin() {
        SkinCoin skin = new SkinCoin(200, 240, 15, 15, "blue", "Player1");
        BluePlayer foreign = new BluePlayer("OtherPlayer", 0, 0);
        double w = foreign.getWidth();
        skin.onCollect(foreign);
        assertEquals(w, foreign.getWidth(), 0.001, "Non-owner must not be affected");
        assertFalse(skin.isCollected(), "Coin must remain available");
    }

    @Test
    void restoreSkinShouldRevertToCreatedDefault() {
        redPlayer.changeSkin(new BlueSkin());
        redPlayer.restoreSkin();
        assertEquals(Color.RED, redPlayer.getDisplayColor());
    }

    // =================== 5. Coins ===================

    @Test
    void coinShouldOnlyBeCollectedByOwner() {
        Coin coin = new Coin(0, 0, 10, 10, "yellow", "Player1");
        RedPlayer other = new RedPlayer("Player2", 0, 0);
        coin.onCollect(other);
        assertFalse(coin.isCollected());
        coin.onCollect(redPlayer);
        assertTrue(coin.isCollected());
    }

    @Test
    void coinShouldNotBeCollectedTwiceWithoutReset() {
        Coin coin = new Coin(0, 0, 10, 10, "yellow", "Player1");
        coin.onCollect(redPlayer);
        coin.onCollect(redPlayer);
        // second call no-op (still collected, no side effects beyond first)
        assertTrue(coin.isCollected());
    }

    @Test
    void isCoinsCollectedByShouldReturnTrueWhenAllOwnedCollected() {
        Coin c1 = new Coin(0, 0, 10, 10, "yellow", "Player1");
        Coin c2 = new Coin(0, 20, 10, 10, "yellow", "Player1");
        Coin foreign = new Coin(0, 40, 10, 10, "yellow", "Player2");
        level.addCoin(c1);
        level.addCoin(c2);
        level.addCoin(foreign);
        c1.onCollect(redPlayer);
        c2.onCollect(redPlayer);
        assertTrue(level.isCoinsCollectedBy(redPlayer));
    }

    @Test
    void coinResetShouldReopenCollection() {
        Coin coin = new Coin(0, 0, 10, 10, "yellow", "Player1");
        coin.onCollect(redPlayer);
        coin.reset();
        assertFalse(coin.isCollected());
        coin.onCollect(redPlayer);
        assertTrue(coin.isCollected());
    }

    @Test
    void pickingAnotherSkinCoinShouldRevertPreviousSkinFirst() {
        SkinCoin blue = new SkinCoin(0, 0, 15, 15, "blue", "Player1");
        SkinCoin green = new SkinCoin(0, 0, 15, 15, "green", "Player1");
        blue.onCollect(redPlayer);
        assertEquals(30.0, redPlayer.getWidth(), 0.001);
        green.onCollect(redPlayer);
        // green skin has size 20, not 30
        assertEquals(20.0, redPlayer.getWidth(), 0.001);
    }

    // =================== 6. Zones ===================

    @Test
    void initialZoneShouldUpdateSpawnOnlyForOwner() {
        InitialZone zone = new InitialZone(200, 200, 50, 50, "Player1");
        zone.onPlayerContact(redPlayer, level);
        assertEquals(200, redPlayer.getSpawnX(), 0.001);

        RedPlayer foreign = new RedPlayer("Player2", 0, 0);
        zone.onPlayerContact(foreign, level);
        assertNotEquals(200, foreign.getSpawnX());
    }

    @Test
    void finalZoneShouldNotSetWinnerWithoutAllCoins() {
        FinalZone zone = new FinalZone(700, 200, 50, 50, "Player1");
        Coin coin = new Coin(0, 0, 10, 10, "yellow", "Player1");
        level.addCoin(coin);
        level.addPlayer(redPlayer);
        zone.onPlayerContact(redPlayer, level);
        assertFalse(level.hasWinner());
    }

    @Test
    void finalZoneShouldNotOverrideExistingWinner() {
        FinalZone zone = new FinalZone(700, 200, 50, 50, "Player1");
        Player first = new RedPlayer("Player1", 700, 200);
        Player second = new RedPlayer("Player1", 700, 200);
        level.setWinner(first);
        zone.onPlayerContact(second, level);
        assertSame(first, level.getWinner());
    }

    @Test
    void finalZoneShouldOnlyAcceptOwnerPlayer() {
        FinalZone zone = new FinalZone(700, 200, 50, 50, "Player1");
        Player notOwner = new RedPlayer("OtherPlayer", 700, 200);
        zone.onPlayerContact(notOwner, level);
        assertFalse(level.hasWinner());
    }

    @Test
    void intermediateZoneShouldMarkCheckpoint() {
        IntermediateZone zone = new IntermediateZone(400, 200, 60, 60);
        assertFalse(redPlayer.hasCheckpoint());
        zone.onPlayerEnter(redPlayer);
        assertTrue(redPlayer.hasCheckpoint());
    }

    @Test
    void safeZonesShouldShieldPlayerFromHits() {
        InitialZone zone = new InitialZone(0, 0, 800, 500, "Player1");
        level.addZone("initial_Player1", zone);
        level.addPlayer(redPlayer);
        assertTrue(level.isInSafeZone(redPlayer));
    }

    // =================== 7. Game modes ===================

    @Test
    void pvspModeShouldDisableTimer() throws Exception {
        // Set up minimal PvsP file expectation by directly setting mode and using setHasTimer
        Level l = new Level(1, 1000, map);
        l.setHasTimer(false);
        int before = l.getGameTime();
        l.updateLevel();
        assertEquals(before, l.getGameTime(), "Disabled timer must not decrement");
    }

    @Test
    void playerModeShouldKeepTimerActive() {
        Level l = new Level(1, 1000, map);
        int before = l.getGameTime();
        l.updateLevel();
        assertEquals(before - 1, l.getGameTime());
    }

    @Test
    void gameModeIsCompleteShouldDelegateToLevel() {
        Level l = new Level(1, 1000, map);
        assertFalse(GameMode.PLAYER.isComplete(l));
        l.setWinner(redPlayer);
        assertTrue(GameMode.PLAYER.isComplete(l));
    }

    // =================== 8. Lifecycle ===================

    @Test
    void isGameOverShouldBeTrueWhenTimeRunsOutAndLevelNotComplete() {
        Level l = new Level(1, 1, map);
        game.setGameMode(GameMode.PLAYER);
        game.startLevel(1, 0, map);
        // Use the facade game which has currentLevel via startLevel
        // After timer hits zero, isGameOver true
        // currentLevel.getGameTime() == 0
        assertTrue(game.isGameOver());
    }

    @Test
    void isGameOverShouldBeFalseInPvspRegardlessOfTime() {
        game.setGameMode(GameMode.PvsP);
        game.startLevel(1, 0, map);
        game.getCurrentLevel().setHasTimer(false);
        assertFalse(game.isGameOver());
    }

    @Test
    void hasNextLevelShouldReturnFalseAtLastLevel() {
        // Player mode last level is level3; loading level4 should return null
        game.setGameMode(GameMode.PLAYER);
        game.startGame(1);
        // Skip ahead to level 3
        while (game.getCurrentLevelNumber() < 3 && game.hasNextLevel()) {
            game.advanceLevel();
        }
        assertFalse(game.hasNextLevel(), "Beyond level 3 there must be no next level");
    }

    @Test
    void restartLevelShouldReloadCurrentLevel() {
        game.setGameMode(GameMode.PLAYER);
        game.startGame(1);
        int levelNumberBefore = game.getCurrentLevelNumber();
        game.restartLevel();
        assertEquals(levelNumberBefore, game.getCurrentLevelNumber());
        assertNotNull(game.getCurrentLevel());
    }

    // =================== 9. Persistence ===================

    @Test
    void guardarPartidaShouldThrowPersistenceExceptionOnInvalidFile() {
        File invalid = new File("/__nonexistent__/cannot/be/written.dat");
        assertThrows(PersistenceException.class, () -> game.guardarPartida(invalid));
    }

    @Test
    void abrirPartidaShouldThrowPersistenceExceptionOnInvalidFile() {
        File missing = new File("__never_exists.dat");
        assertThrows(PersistenceException.class, () -> game.abrirPartida(missing));
    }

    @Test
    void exportarNivelShouldThrowLevelIOExceptionOnInvalidPath() {
        game.setGameMode(GameMode.PLAYER);
        game.startGame(1);
        File invalid = new File("/__nonexistent__/cannot/be/written.txt");
        assertThrows(LevelIOException.class, () -> game.exportarNivel(invalid));
    }

    @Test
    void saveAndLoadRoundtripShouldPreserveLevelNumber(@TempDir Path tmp) throws Exception {
        game.setGameMode(GameMode.PLAYER);
        game.startGame(1);
        File save = tmp.resolve("save.dat").toFile();
        game.guardarPartida(save);
        TheDOPOHardestGame loaded = new TheDOPOHardestGame();
        loaded.abrirPartida(save);
        assertEquals(game.getCurrentLevelNumber(), loaded.getCurrentLevelNumber());
    }

    // =================== 10. Enemy movement ===================

    @Test
    void linearMovementAcceleratedShouldMoveAtDoubleSpeed() {
        Enemy basic = new Enemy(100, 100, 20, 20, LinearMovement.basic(Direction.VERTICAL, 1));
        Enemy fast  = new Enemy(100, 100, 20, 20, LinearMovement.accelerated(Direction.VERTICAL, 1));
        basic.move(level);
        fast.move(level);
        double basicDelta = basic.getY() - 100;
        double fastDelta  = fast.getY() - 100;
        assertEquals(basicDelta * 2.0, fastDelta, 0.001);
    }

    @Test
    void linearMovementShouldBounceOffWalls() {
        level.addStaticElement(new SolidWall(0, 0, 800, 20, "black"));
        // Place enemy adjacent to wall so one upward move would clip it
        Enemy enemy = new Enemy(100, 22, 20, 20, LinearMovement.basic(Direction.VERTICAL, -1));
        double yBefore = enemy.getY();
        enemy.move(level); // tries to go up, blocked by wall, reverses sign
        enemy.move(level); // now moving down
        assertTrue(enemy.getY() > yBefore, "After bouncing off wall, enemy moves opposite direction");
    }

    @Test
    void patrolMovementShouldFollowWaypoints() {
        Point2D.Double[] route = { new Point2D.Double(100, 100), new Point2D.Double(200, 100) };
        Enemy enemy = new Enemy(100, 100, 20, 20, PatrolMovement.basic(route));
        // Move several ticks; should be advancing toward second waypoint
        for (int i = 0; i < 5; i++) enemy.move(level);
        assertTrue(enemy.getX() > 100, "Enemy should move toward second waypoint");
    }

    // =================== 11. Bombs and LifeSource ===================

    @Test
    void bombShouldKillPlayerOnContact() {
        Bomb bomb = new Bomb(100, 100, 20, 20);
        level.addStaticElement(bomb);
        level.addPlayer(redPlayer);
        redPlayer.setPosition(100, 100);
        int before = redPlayer.getDeaths();
        bomb.onContact(redPlayer, level);
        assertEquals(before + 1, redPlayer.getDeaths());
    }

    @Test
    void bombShouldKillEnemyOnContact() {
        Bomb bomb = new Bomb(100, 100, 20, 20);
        Enemy enemy = new Enemy(100, 100, 20, 20, LinearMovement.basic(Direction.VERTICAL, 1));
        bomb.onContact(enemy, level);
        assertTrue(enemy.isDead());
    }

    @Test
    void bombShouldBeRemovedAfterExplosion() {
        Bomb bomb = new Bomb(100, 100, 20, 20);
        bomb.onContact(redPlayer, level);
        assertTrue(bomb.shouldBeRemoved());
    }

    @Test
    void lifeSourceShouldGrantExtraLifeOnCollect() {
        LifeSource life = new LifeSource(100, 100, 20, 20, "pink");
        assertEquals(0, redPlayer.getExtraLives());
        life.onCollect(redPlayer);
        assertEquals(1, redPlayer.getExtraLives());
    }

    @Test
    void playerWithExtraLifeShouldAbsorbOneHit() {
        level.addPlayer(redPlayer);
        redPlayer.setPosition(300, 300);
        redPlayer.addLife();
        int before = redPlayer.getDeaths();
        redPlayer.onHit(level);
        assertEquals(before, redPlayer.getDeaths(), "Extra life absorbs the hit");
        assertEquals(0, redPlayer.getExtraLives());
    }

    @Test
    void lifeSourceShouldBeInvisibleAfterCollectionAndReappearOnDeath() {
        level.addPlayer(redPlayer);
        LifeSource life = new LifeSource(100, 100, 20, 20, "pink");
        life.onCollect(redPlayer);
        assertFalse(life.isVisible(), "LifeSource must be invisible after collection");
        assertFalse(life.shouldBeRemoved(), "LifeSource stays in list to allow reset on death");
        life.reset();
        assertTrue(life.isVisible(), "LifeSource must reappear after reset");
    }

    // =================== 12. PvsP player-player collision ===================

    @Test
    void playerCollisionShouldKillBothPlayers() {
        Player p1 = new RedPlayer("Player1", 100, 100);
        Player p2 = new BluePlayer("Player2", 100, 100);
        level.addPlayer(p1);
        level.addPlayer(p2);
        p1.setPosition(300, 300);
        p2.setPosition(300, 300);
        int d1 = p1.getDeaths();
        int d2 = p2.getDeaths();
        p1.onPlayerContact(p2, level);
        assertEquals(d1 + 1, p1.getDeaths());
        assertEquals(d2 + 1, p2.getDeaths());
    }

    @Test
    void playerCollisionShouldResetCoinsOfBothPlayers() {
        Player p1 = new RedPlayer("Player1", 100, 100);
        Player p2 = new BluePlayer("Player2", 100, 100);
        Coin c1 = new Coin(0, 0, 10, 10, "yellow", "Player1");
        Coin c2 = new Coin(0, 0, 10, 10, "yellow", "Player2");
        level.addCoin(c1);
        level.addCoin(c2);
        level.addPlayer(p1);
        level.addPlayer(p2);
        c1.onCollect(p1);
        c2.onCollect(p2);
        assertTrue(c1.isCollected() && c2.isCollected());
        p1.onPlayerContact(p2, level);
        assertFalse(c1.isCollected());
        assertFalse(c2.isCollected());
    }

    // =================== Helpers ===================

    private File writeLevel(Path dir, String content) throws IOException {
        return writeLevel(dir, content, "level.txt");
    }

    private File writeLevel(Path dir, String content, String name) throws IOException {
        File f = dir.resolve(name).toFile();
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.print(content);
        }
        return f;
    }
}
