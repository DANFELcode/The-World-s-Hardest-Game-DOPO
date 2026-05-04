package domain;

/**
 * Represents a random game strategy for the machine player. Picks moves at random.
 * ()
 * Inv: true
 */
public class RandomStrategy implements GameStrategy {

    /**
     * Executes a random move for the machine.
     * @param machine the machine player
     * @param level the current level
     */
    @Override
    public void execute(Machine machine, Level level) {}
}
