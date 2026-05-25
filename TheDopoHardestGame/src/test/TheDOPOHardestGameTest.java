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
        Coin coin = new YellowCoin(200, 240, 12, 12, "yellow", "Player1");
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
        Coin coin = new YellowCoin(200, 240, 12, 12, "yellow", "Player1");
        level.addCoin(coin);
        level.addPlayer(redPlayer);
        coin.setOwnerPlayer(redPlayer);
        coin.onCollect(redPlayer);
        redPlayer.markCheckpoint(300, 300);
        level.protectCollectedCoins(redPlayer);
        redPlayer.die(level);
        assertTrue(coin.isCollected(), "Normal coin should survive death after checkpoint");
    }

    @Test
    void skinCoinShouldResetOnDeathWithoutCheckpoint() {
        SkinCoin skinCoin = new SkinCoin(200, 240, 15, 15, "green", "Player1");
        level.addCoin(skinCoin);
        level.addPlayer(redPlayer);
        skinCoin.setOwnerPlayer(redPlayer);
        skinCoin.onCollect(redPlayer);
        redPlayer.die(level);
        assertFalse(skinCoin.isCollected(), "SkinCoin must reset on death without checkpoint");
    }

    @Test
    void skinCoinShouldNotResetOnDeathWithCheckpoint() {
        SkinCoin skinCoin = new SkinCoin(200, 240, 15, 15, "green", "Player1");
        level.addCoin(skinCoin);
        level.addPlayer(redPlayer);
        skinCoin.setOwnerPlayer(redPlayer);
        skinCoin.onCollect(redPlayer);
        redPlayer.markCheckpoint(300, 300);
        level.protectCollectedCoins(redPlayer);
        redPlayer.die(level);
        assertTrue(skinCoin.isCollected(), "SkinCoin must stay collected after death with checkpoint");
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
        Coin coin = new YellowCoin(0, 0, 10, 10, "yellow", "Player1");
        RedPlayer other = new RedPlayer("Player2", 0, 0);
        coin.onCollect(other);
        assertFalse(coin.isCollected());
        coin.onCollect(redPlayer);
        assertTrue(coin.isCollected());
    }

    @Test
    void coinShouldNotBeCollectedTwiceWithoutReset() {
        Coin coin = new YellowCoin(0, 0, 10, 10, "yellow", "Player1");
        coin.onCollect(redPlayer);
        coin.onCollect(redPlayer);
        // second call no-op (still collected, no side effects beyond first)
        assertTrue(coin.isCollected());
    }

    @Test
    void isCoinsCollectedByShouldReturnTrueWhenAllOwnedCollected() {
        Coin c1 = new YellowCoin(0, 0, 10, 10, "yellow", "Player1");
        Coin c2 = new YellowCoin(0, 20, 10, 10, "yellow", "Player1");
        Coin foreign = new YellowCoin(0, 40, 10, 10, "yellow", "Player2");
        level.addCoin(c1);
        level.addCoin(c2);
        level.addCoin(foreign);
        c1.onCollect(redPlayer);
        c2.onCollect(redPlayer);
        assertTrue(level.isCoinsCollectedBy(redPlayer));
    }

    @Test
    void coinResetShouldReopenCollection() {
        Coin coin = new YellowCoin(0, 0, 10, 10, "yellow", "Player1");
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
        Coin coin = new YellowCoin(0, 0, 10, 10, "yellow", "Player1");
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
    void bombShouldTurnInvisibleAfterExplosion() {
        Bomb bomb = new Bomb(100, 100, 20, 20);
        bomb.onContact(redPlayer, level);
        assertFalse(bomb.shouldBeRemoved(), "An exploded bomb is kept so it can regenerate on respawn");
        assertFalse(bomb.isVisible(), "An exploded bomb turns invisible");
        assertTrue(bomb.hasExploded());
    }

    @Test
    void lifeSourceShouldGrantExtraLifeOnCollect() {
        LifeSource life = new LifeSource(100, 100, 20, 20, "pink", "Player1");
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
        LifeSource life = new LifeSource(100, 100, 20, 20, "pink", "Player1");
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
        Coin c1 = new YellowCoin(0, 0, 10, 10, "yellow", "Player1");
        Coin c2 = new YellowCoin(0, 0, 10, 10, "yellow", "Player2");
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

    // =================== 13. Facade getters with a loaded level ===================

    @Test
    void facadeGettersShouldReportLevelStateAfterStartGame() {
        game.setGameMode(GameMode.PLAYER);
        game.startGame(1);
        assertEquals(1, game.getLevelNumber());
        assertNotNull(game.getCurrentLevel());
        assertTrue(game.getTotalCoins() >= 0);
        assertEquals(0, game.getCollectedCoins());
        assertFalse(game.getPlayers().isEmpty());
        assertFalse(game.getDrawCommands().isEmpty());
        assertFalse(game.isLevelComplete());
        assertNull(game.getLevelWinner());
        assertNotNull(game.getBackgroundColor());
        assertNotNull(game.getLevelsWon());
    }

    @Test
    void facadePlayerGettersShouldReturnZeroAtStart() {
        game.setGameMode(GameMode.PLAYER);
        game.startGame(1);
        assertEquals(0, game.getPlayerDeaths(0));
        assertEquals(0, game.getPlayerCoins(0));
        assertEquals(0, game.getPlayerLifetimeCoins(0));
        assertTrue(game.getPlayerTotalCoins(0) >= 0);
        assertTrue(game.getRemainingTime() > 0);
    }

    @Test
    void facadeGettersShouldBeSafeWithoutLevel() {
        TheDOPOHardestGame g = new TheDOPOHardestGame();
        assertEquals(0, g.getLevelNumber());
        assertEquals(0, g.getTotalCoins());
        assertEquals(0, g.getCollectedCoins());
        assertEquals(0, g.getPlayerDeaths(0));
        assertEquals(0, g.getPlayerCoins(0));
        assertEquals(0, g.getPlayerLifetimeCoins(0));
        assertEquals(0, g.getPlayerTotalCoins(0));
        assertEquals(0.0, g.getRemainingTime(), 0.001);
        assertTrue(g.getPlayers().isEmpty());
        assertTrue(g.getDrawCommands().isEmpty());
        assertFalse(g.isLevelComplete());
        assertNull(g.getLevelWinner());
        assertFalse(g.isPaused());
        assertNotNull(g.getBackgroundColor());
    }

    @Test
    void facadeOutOfRangePlayerIndexShouldReturnZero() {
        game.setGameMode(GameMode.PLAYER);
        game.startGame(1);
        assertEquals(0, game.getPlayerDeaths(99));
        assertEquals(0, game.getPlayerCoins(-1));
        assertEquals(0, game.getPlayerLifetimeCoins(50));
        assertEquals(0, game.getPlayerTotalCoins(50));
    }

    @Test
    void updateShouldAdvanceLevelTime() {
        game.setGameMode(GameMode.PLAYER);
        game.startGame(1);
        double before = game.getRemainingTime();
        game.update();
        assertTrue(game.getRemainingTime() <= before);
    }

    @Test
    void movePlayerWhilePausedShouldNotMove() {
        game.setGameMode(GameMode.PLAYER);
        game.startGame(1);
        Player p = game.getPlayers().get(0);
        game.togglePause();
        double sx = p.getX(), sy = p.getY();
        game.movePlayer(0, 1, 0);
        assertEquals(sx, p.getX(), 0.001);
        assertEquals(sy, p.getY(), 0.001);
    }

    @Test
    void movePlayerWithInvalidIndexShouldBeSafe() {
        game.setGameMode(GameMode.PLAYER);
        game.startGame(1);
        game.movePlayer(99, 1, 0);   // no exception
        game.movePlayer(0, 1, 0);
        assertFalse(game.getPlayers().isEmpty());
    }

    @Test
    void togglePauseShouldFlipFacadePauseState() {
        game.setGameMode(GameMode.PLAYER);
        game.startGame(1);
        assertFalse(game.isPaused());
        game.togglePause();
        assertTrue(game.isPaused());
        game.togglePause();
        assertFalse(game.isPaused());
    }

    // =================== 14. Level selector ===================

    @Test
    void getAvailableLevelCountShouldCountModeLevels() {
        game.setGameMode(GameMode.PLAYER);
        assertTrue(game.getAvailableLevelCount() >= 3);
        game.setGameMode(GameMode.PvsP);
        assertTrue(game.getAvailableLevelCount() >= 2);
    }

    @Test
    void startGameAtSpecificLevelShouldLoadThatLevel() {
        game.setGameMode(GameMode.PLAYER);
        game.startGame(2);
        assertEquals(2, game.getLevelNumber());
    }

    // =================== 15. Persistence (text format) ===================

    @Test
    void saveAndLoadShouldPreservePlayerStats(@TempDir Path tmp) throws Exception {
        game.setGameMode(GameMode.PLAYER);
        game.setPlayerType("Player1", "blue");
        game.startGame(1);
        game.getPlayers().get(0).die(game.getCurrentLevel());
        game.getPlayers().get(0).die(game.getCurrentLevel());
        File save = tmp.resolve("s.dat").toFile();
        game.guardarPartida(save);

        TheDOPOHardestGame loaded = new TheDOPOHardestGame();
        loaded.abrirPartida(save);
        assertEquals(2, loaded.getPlayerDeaths(0));
        assertEquals("blue", loaded.getPlayers().get(0).getTypeName());
        assertEquals(GameMode.PLAYER, loaded.getGameMode());
    }

    @Test
    void saveAndLoadShouldPreserveBorderColor(@TempDir Path tmp) throws Exception {
        game.setGameMode(GameMode.PLAYER);
        game.setPlayerBorderColor("Player1", Color.CYAN);
        game.startGame(1);
        File save = tmp.resolve("s.dat").toFile();
        game.guardarPartida(save);

        TheDOPOHardestGame loaded = new TheDOPOHardestGame();
        loaded.abrirPartida(save);
        assertEquals(Color.CYAN, loaded.getPlayers().get(0).getBorderColor());
    }

    @Test
    void importarNivelShouldLoadLevelFromFile(@TempDir Path tmp) throws Exception {
        File f = writeLevel(tmp, "NUMBER=7\nTIME=30\n");
        game.importarNivel(f);
        assertNotNull(game.getCurrentLevel());
        assertEquals(7, game.getLevelNumber());
    }

    // =================== 16. Player type / border configuration ===================

    @Test
    void setPlayerTypeShouldDetermineCreatedPlayerType() {
        game.setGameMode(GameMode.PLAYER);
        game.setPlayerType("Player1", "green");
        game.startGame(1);
        assertEquals("green", game.getPlayers().get(0).getTypeName());
    }

    @Test
    void setPlayerBorderColorShouldApplyToCreatedPlayer() {
        game.setGameMode(GameMode.PLAYER);
        game.setPlayerBorderColor("Player1", Color.MAGENTA);
        game.startGame(1);
        assertEquals(Color.MAGENTA, game.getPlayers().get(0).getBorderColor());
    }

    @Test
    void playerCreateFactoryShouldBuildCorrectSubtype() {
        assertEquals("blue",  Player.create("blue",  "P", 0, 0).getTypeName());
        assertEquals("green", Player.create("green", "P", 0, 0).getTypeName());
        assertEquals("red",   Player.create("red",   "P", 0, 0).getTypeName());
        assertEquals("red",   Player.create("unknown", "P", 0, 0).getTypeName());
    }

    // =================== 17. PvsM machine mode ===================

    @Test
    void pvsmModeShouldMakePlayer2AMachine() {
        game.setGameMode(GameMode.PvsM);
        game.startGame(1);
        Player machine = null;
        for (Player p : game.getPlayers()) {
            if (p.getName().equals("Player2")) machine = p;
        }
        assertNotNull(machine);
        assertTrue(machine.isMachine());
    }

    @Test
    void pvsmModeShouldDisableTimer() {
        game.setGameMode(GameMode.PvsM);
        game.startGame(1);
        assertFalse(game.getCurrentLevel().hasTimer());
    }

    @Test
    void playerWithoutStrategyShouldNotBeMachine() {
        RedPlayer p = new RedPlayer("P", 400, 240);
        assertFalse(p.isMachine());
        p.setStrategy(new RandomStrategy());
        assertTrue(p.isMachine());
    }

    @Test
    void automateWithoutStrategyShouldBeNoOp() {
        RedPlayer p = new RedPlayer("P", 400, 240);
        level.addPlayer(p);
        double x = p.getX(), y = p.getY();
        p.automate(level);
        assertEquals(x, p.getX(), 0.001);
        assertEquals(y, p.getY(), 0.001);
    }

    @Test
    void randomStrategyShouldMoveThePlayer() {
        RedPlayer p = new RedPlayer("P", 400, 240);
        level.addPlayer(p);
        p.setStrategy(new RandomStrategy());
        double sx = p.getX(), sy = p.getY();
        for (int i = 0; i < 20; i++) p.automate(level);
        assertTrue(p.getX() != sx || p.getY() != sy, "Machine should move from its start position");
    }

    @Test
    void updateLevelShouldAutomateMachinePlayers() {
        RedPlayer p = new RedPlayer("P", 400, 240);
        p.setStrategy(new RandomStrategy());
        level.addPlayer(p);
        double sx = p.getX(), sy = p.getY();
        for (int i = 0; i < 20; i++) level.updateLevel();
        assertTrue(p.getX() != sx || p.getY() != sy);
    }

    // =================== 18. Pause logic on Level ===================

    @Test
    void levelTogglePauseShouldFlipState() {
        Level l = new Level(1, 1000, map);
        assertFalse(l.isPaused());
        l.togglePause();
        assertTrue(l.isPaused());
    }

    @Test
    void pausedLevelShouldNotAdvanceTime() {
        Level l = new Level(1, 1000, map);
        l.togglePause();
        int t = l.getGameTime();
        l.updateLevel();
        assertEquals(t, l.getGameTime(), "Paused level must not advance time");
    }

    // =================== 19. Diagonal movement normalization ===================

    @Test
    void diagonalMoveShouldBeNormalizedToSpeed() {
        level.addPlayer(redPlayer);
        double sx = redPlayer.getX(), sy = redPlayer.getY();
        redPlayer.move(1, 1, level);
        double dx = redPlayer.getX() - sx;
        double dy = redPlayer.getY() - sy;
        double magnitude = Math.sqrt(dx * dx + dy * dy);
        assertEquals(redPlayer.getSpeed(), magnitude, 0.001,
            "Diagonal movement must be normalized to one speed unit");
    }

    // =================== 20. GameMap background color ===================

    @Test
    void gameMapShouldHaveDefaultBackgroundColor() {
        GameMap m = new GameMap(800, 500);
        assertEquals(GameConstants.COLOR_BOARD, m.getBackgroundColor());
    }

    @Test
    void gameMapBackgroundColorShouldBeSettable() {
        GameMap m = new GameMap(800, 500);
        m.setBackgroundColor(Color.RED);
        assertEquals(Color.RED, m.getBackgroundColor());
    }

    // =================== 21. SkinBehavior factory and checkpoint skin ===================

    @Test
    void skinBehaviorFactoryShouldBuildRequestedSkin() {
        RedPlayer p = new RedPlayer("P", 0, 0);
        SkinBehavior.of("blue").apply(p);
        assertEquals(30.0, p.getWidth(), 0.001);
        assertEquals(Color.BLUE, SkinBehavior.of("blue").getDisplayColor());
    }

    @Test
    void skinBehaviorFactoryShouldFallBackToDefault() {
        assertNotNull(SkinBehavior.of(null));
        assertNotNull(SkinBehavior.of("nonexistent"));
    }

    @Test
    void deathWithCheckpointShouldRestoreLastSkin() {
        SkinCoin blue = new SkinCoin(0, 0, 15, 15, "blue", "Player1");
        level.addCoin(blue);
        level.addPlayer(redPlayer);
        blue.onCollect(redPlayer);
        assertEquals(Color.BLUE, redPlayer.getDisplayColor());
        redPlayer.markCheckpoint(300, 300);
        redPlayer.die(level);
        assertEquals(Color.BLUE, redPlayer.getDisplayColor(),
            "Death with checkpoint keeps the last collected skin");
    }

    @Test
    void deathWithoutCheckpointShouldRevertToDefaultSkin() {
        SkinCoin blue = new SkinCoin(0, 0, 15, 15, "blue", "Player1");
        level.addCoin(blue);
        level.addPlayer(redPlayer);
        blue.onCollect(redPlayer);
        redPlayer.die(level);
        assertEquals(Color.RED, redPlayer.getDisplayColor(),
            "Death without checkpoint reverts to the default skin");
    }

    // =================== 22. LifeSource ownership ===================

    @Test
    void lifeSourceShouldNotBeCollectedByNonOwner() {
        LifeSource life = new LifeSource(100, 100, 20, 20, "pink", "Player1");
        BluePlayer notOwner = new BluePlayer("Player2", 0, 0);
        life.onCollect(notOwner);
        assertEquals(0, notOwner.getExtraLives());
        assertTrue(life.isVisible(), "LifeSource must remain available");
    }

    @Test
    void lifeSourceShouldReappearOnOwnerDeath() {
        LifeSource life = new LifeSource(100, 100, 20, 20, "pink", "Player1");
        level.addStaticElement(life);
        level.addPlayer(redPlayer);
        life.onCollect(redPlayer);
        assertFalse(life.isVisible());
        redPlayer.die(level);
        assertTrue(life.isVisible(), "Owner's LifeSource reappears on death");
    }

    @Test
    void lifeSourceShouldNotReappearOnOtherPlayerDeath() {
        LifeSource life = new LifeSource(100, 100, 20, 20, "pink", "Player1");
        level.addStaticElement(life);
        Player p1 = new RedPlayer("Player1", 50, 240);
        Player p2 = new BluePlayer("Player2", 100, 240);
        level.addPlayer(p1);
        level.addPlayer(p2);
        life.onCollect(p1);
        p2.die(level);
        assertFalse(life.isVisible(), "Another player's death must not reset it");
    }

    @Test
    void lifeSourceExtraFileParamsShouldIncludeOwner() {
        LifeSource life = new LifeSource(0, 0, 20, 20, "pink", "Player2");
        assertTrue(life.extraFileParams().contains("owner=Player2"));
    }

    @Test
    void unownedStaticElementShouldReturnNullOwner() {
        SolidWall wall = new SolidWall(0, 0, 10, 10, "black");
        assertNull(wall.getOwnerName());
    }

    // =================== 23. Player-player collision inside zones ===================

    @Test
    void playerCollisionInsideZoneShouldNotKill() {
        InitialZone zone = new InitialZone(0, 0, 800, 500, "Player1");
        level.addZone("initial_Player1", zone);
        Player p1 = new RedPlayer("Player1", 100, 100);
        Player p2 = new BluePlayer("Player2", 100, 100);
        level.addPlayer(p1);
        level.addPlayer(p2);
        int d1 = p1.getDeaths();
        p1.onPlayerContact(p2, level);
        assertEquals(d1, p1.getDeaths(), "Players must not kill each other inside a zone");
    }

    // =================== 24. Misc coverage ===================

    @Test
    void gameConstantsShouldExposeColorsAndSizes() {
        assertNotNull(GameConstants.COLOR_BOMB);
        assertNotNull(GameConstants.COLOR_COIN);
        assertEquals(20.0, GameConstants.MIN_PLAYER_SIZE, 0.001);
    }

    @Test
    void playerToDrawCommandShouldNotBeNull() {
        assertNotNull(redPlayer.toDrawCommand());
        assertNotNull(bluePlayer.toDrawCommand());
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

    // =================== 17. DrawCommand ===================
    @Test
    void testDrawCommandConstructorWithBasicParameters() {
        dto.DrawCommand cmd = new dto.DrawCommand(Color.RED, 10, 20, 30, 40, dto.DrawCommand.Shape.RECT);
        assertEquals(Color.RED, cmd.color);
        assertEquals(10, cmd.x);
        assertEquals(20, cmd.y);
        assertEquals(30, cmd.width);
        assertEquals(40, cmd.height);
        assertEquals(dto.DrawCommand.Shape.RECT, cmd.shape);
        assertNull(cmd.borderColor);
        assertNull(cmd.outerBorderColor);
    }

    @Test
    void testDrawCommandConstructorWithBorderColor() {
        dto.DrawCommand cmd = new dto.DrawCommand(Color.BLUE, 0, 0, 10, 10, dto.DrawCommand.Shape.OVAL, Color.BLACK);
        assertEquals(Color.BLUE, cmd.color);
        assertEquals(Color.BLACK, cmd.borderColor);
        assertNull(cmd.outerBorderColor);
    }

    @Test
    void testDrawCommandConstructorWithOuterBorderColor() {
        dto.DrawCommand cmd = new dto.DrawCommand(Color.GREEN, 5, 5, 15, 15, dto.DrawCommand.Shape.RECT, Color.WHITE, Color.YELLOW);
        assertEquals(Color.GREEN, cmd.color);
        assertEquals(Color.WHITE, cmd.borderColor);
        assertEquals(Color.YELLOW, cmd.outerBorderColor);
    }

    // =================== 18. Machine ===================
    @Test
    void testMachineCreation() {
        GameStrategy strategy = new RandomStrategy();
        Machine machine = new Machine("Bot1", 100, 150, 20, 20, 2.5, strategy);
        
        assertEquals("Bot1", machine.getName());
        assertEquals(100.0, machine.getX());
        assertEquals(150.0, machine.getY());
        assertEquals(20.0, machine.getWidth());
        assertEquals(20.0, machine.getHeight());
        assertEquals(3.0, machine.getSpeed());
    }

    @Test
    void testMachineGetTypeName() {
        Machine machine = new Machine("Bot", 0, 0, 10, 10, 1, new RandomStrategy());
        assertEquals("machine", machine.getTypeName());
    }

    @Test
    void testMachineGetDisplayColor() {
        Machine machine = new Machine("Bot", 0, 0, 10, 10, 1, new ExpertStrategy());
        assertEquals(Color.MAGENTA, machine.getDisplayColor());
    }

    // =================== 19. GameStrategy ===================
    @Test
    void testGameStrategyOfWithExpert() {
        GameStrategy strategy = GameStrategy.of("expert");
        assertEquals(ExpertStrategy.class, strategy.getClass());
    }

    @Test
    void testGameStrategyOfWithRandom() {
        GameStrategy strategy = GameStrategy.of("random");
        assertEquals(RandomStrategy.class, strategy.getClass());
    }

    @Test
    void testGameStrategyOfWithNullReturnsRandom() {
        GameStrategy strategy = GameStrategy.of(null);
        assertEquals(RandomStrategy.class, strategy.getClass());
    }

    @Test
    void testGameStrategyOfWithUnknownReturnsRandom() {
        GameStrategy strategy = GameStrategy.of("unknown_strategy");
        assertEquals(RandomStrategy.class, strategy.getClass());
    }

    @Test
    void testGameStrategyOfIsCaseInsensitive() {
        GameStrategy strategy = GameStrategy.of("ExPeRt");
        assertEquals(ExpertStrategy.class, strategy.getClass());
    }

    // =================== 20. PatrolMovement ===================
    @Test
    void testPatrolMovementCreation() {
        Point2D.Double[] route = { new Point2D.Double(10, 10), new Point2D.Double(20, 10) };
        PatrolMovement patrol = PatrolMovement.basic(route);
        assertNotNull(patrol);
    }

    @Test
    void testPatrolMovementCreationWithEmptyRouteThrowsException() {
        Point2D.Double[] route = new Point2D.Double[0];
        assertThrows(IllegalArgumentException.class, () -> PatrolMovement.basic(route));
    }

    @Test
    void testPatrolMovementCreationWithNullRouteThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> PatrolMovement.basic(null));
    }

    @Test
    void testPatrolMovementMoveTowardsWaypoint() {
        Point2D.Double[] route = { new Point2D.Double(50, 50), new Point2D.Double(100, 50) };
        PatrolMovement patrol = PatrolMovement.basic(route);
        Enemy enemy = new Enemy(0, 50, 20, 20, patrol);
        
        enemy.move(level);
        assertTrue(enemy.getX() > 0);
        assertEquals(50.0, enemy.getY(), 0.001);
    }

    @Test
    void testPatrolMovementReachWaypointAndCycle() {
        Point2D.Double[] route = { new Point2D.Double(10, 10), new Point2D.Double(50, 10) };
        PatrolMovement patrol = PatrolMovement.basic(route);
        Enemy enemy = new Enemy(8, 10, 20, 20, patrol);
        
        for (int i = 0; i < 10; i++) {
            enemy.move(level);
        }
        
        assertTrue(enemy.getX() > 10.0);
    }

    @Test
    void testPatrolMovementToFileParams() {
        Point2D.Double[] route = { new Point2D.Double(10, 20), new Point2D.Double(30, 40) };
        PatrolMovement patrol = PatrolMovement.basic(route);
        String params = patrol.toFileParams();
        assertEquals("movement=patrol,route=10.0:20.0|30.0:40.0", params);
    }

    // =================== 21. StaticElement ===================
    private static class TestStaticElement extends StaticElement {
        public TestStaticElement(double x, double y, double width, double height, String color) {
            super(x, y, width, height, color);
        }
    }

    @Test
    void testStaticElementBasicGetters() {
        StaticElement el = new TestStaticElement(10, 20, 30, 40, "black");
        assertEquals(10.0, el.getX());
        assertEquals(20.0, el.getY());
        assertEquals(30.0, el.getWidth());
        assertEquals(40.0, el.getHeight());
        assertEquals("black", el.getColor());
    }

    @Test
    void testStaticElementSetters() {
        StaticElement el = new TestStaticElement(0, 0, 10, 10, "white");
        el.setX(100);
        el.setY(200);
        assertEquals(100.0, el.getX());
        assertEquals(200.0, el.getY());
    }

    @Test
    void testStaticElementGetAreaColision() {
        StaticElement el = new TestStaticElement(5, 5, 15, 15, "red");
        java.awt.geom.Rectangle2D rect = el.getAreaColision();
        assertEquals(5.0, rect.getX());
        assertEquals(5.0, rect.getY());
        assertEquals(15.0, rect.getWidth());
        assertEquals(15.0, rect.getHeight());
    }

    @Test
    void testStaticElementDefaultMethods() {
        StaticElement el = new TestStaticElement(0, 0, 10, 10, "black");
        assertFalse(el.shouldBeRemoved());
        assertFalse(el.isBlocking());
        assertTrue(el.isVisible());
        assertFalse(el.isHazardous());
        assertNull(el.getOwnerName());
        assertEquals("WALL", el.getFileType());
        assertEquals("", el.extraFileParams());
        assertEquals(Color.BLACK, el.getDisplayColor());
    }

    @Test
    void testStaticElementToDrawCommand() {
        StaticElement el = new TestStaticElement(1, 2, 3, 4, "black");
        dto.DrawCommand cmd = el.toDrawCommand();
        assertEquals(Color.BLACK, cmd.color);
        assertEquals(1, cmd.x);
        assertEquals(2, cmd.y);
        assertEquals(3, cmd.width);
        assertEquals(4, cmd.height);
        assertEquals(dto.DrawCommand.Shape.RECT, cmd.shape);
    }

    // =================== 22. ExpertStrategy ===================
    @Test
    void testExpertStrategyExecuteWithNoTargetShouldNotMove() {
        ExpertStrategy strategy = new ExpertStrategy();
        Machine machine = new Machine("Bot1", 100, 100, 20, 20, 2.0, strategy);
        
        double startX = machine.getX();
        double startY = machine.getY();
        
        strategy.execute(machine, level);
        
        assertEquals(startX, machine.getX(), 0.1);
        assertEquals(startY, machine.getY(), 0.1);
    }

    @Test
    void testExpertStrategyExecuteFindsClosestCoinAndMovesTowardsIt() {
        ExpertStrategy strategy = new ExpertStrategy();
        Machine machine = new Machine("Bot1", 100, 100, 20, 20, 2.0, strategy);
        
        YellowCoin coin = new YellowCoin(150, 100, 10, 10, "yellow", "Bot1");
        level.addCoin(coin);
        level.addPlayer(machine);

        double startX = machine.getX();
        
        strategy.execute(machine, level);
        
        assertTrue(machine.getX() > startX);
    }

    @Test
    void testExpertStrategyExecuteGoesToFinalZoneWhenNoCoinsLeft() {
        ExpertStrategy strategy = new ExpertStrategy();
        Machine machine = new Machine("Bot1", 100, 100, 20, 20, 2.0, strategy);
        
        FinalZone fz = new FinalZone(100, 200, 50, 50, "Bot1");
        level.addZone("final_Bot1", fz);
        level.addPlayer(machine);

        double startY = machine.getY();
        
        strategy.execute(machine, level);
        
        assertTrue(machine.getY() > startY);
    }
}
