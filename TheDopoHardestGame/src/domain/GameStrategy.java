package domain;

/**
 * Defines an AI movement strategy attached to a Player. <br>
 */
public interface GameStrategy {

    /**
     * Decides and applies the player's movement this tick.
     * @param player the player being controlled by the strategy
     * @param level the current level
     */
    void execute(Player player, Level level);

    /** Factory: builds a fresh strategy instance for the given type identifier. */
    static GameStrategy of(String type) {
        if (type == null) return new RandomStrategy();
        switch (type.toLowerCase()) {
            case "expert": return new ExpertStrategy();
            default:       return new RandomStrategy();
        }
    }
}
