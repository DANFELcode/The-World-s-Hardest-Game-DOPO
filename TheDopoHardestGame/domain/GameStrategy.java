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
}
