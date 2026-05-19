package domain;

/**
 * Game modes supported by the application. Each mode declares how many players
 * it requires, whether a machine participates, and how level completion is checked.
 */
public enum GameMode {
    PLAYER(1, false),
    PvsP(2, false),
    PvsM(1, true);

    private final int playerCount;
    private final boolean hasMachine;

    GameMode(int playerCount, boolean hasMachine) {
        this.playerCount = playerCount;
        this.hasMachine = hasMachine;
    }

    /** Default completion rule: delegates to Level. Modes can override for custom behavior. */
    public boolean isComplete(Level level) {
        return level.isLevelComplete();
    }

    public int getPlayerCount() { return playerCount; }
    public boolean hasMachine() { return hasMachine; }
}
