package domain;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an intermediate checkpoint zone within the level. <br>
 * <b>(x, y, width, height, checkedPlayers)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0, checkedPlayers != null
 */
public class IntermediateZone extends Zone {

    /** Tracks which players have already been processed by this zone, to avoid re-triggering on re-entry. */
    private final Set<String> checkedPlayers = new HashSet<>();

    public IntermediateZone(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public Color getDisplayColor() {
        return GameConstants.COLOR_INTERMEDIATE_ZONE;
    }

    /**
     * Marks the zone as visited and updates the player's checkpoint.
     * Only executes once per player; re-entries by the same player are ignored.
     * @param player the player that reached the checkpoint
     */
    @Override
    public void onPlayerEnter(Player player) {
        if (checkedPlayers.contains(player.getName())) return;
        super.onPlayerEnter(player);

        double centerX = this.x + (this.width / 2.0);
        double centerY = this.y + (this.height / 2.0);
        player.markCheckpoint(centerX, centerY);
    }

    /**
     * Extends the default contact handler to also protect coins already collected
     * by the player at the moment the checkpoint is reached. Coins collected after
     * this point will still reset on death.
     * Only executes once per player; re-entries by the same player are ignored.
     * @param player the player that entered the zone
     * @param level the level context used to protect coins
     */
    @Override
    public void onPlayerContact(Player player, Level level) {
        boolean alreadyProcessed = checkedPlayers.contains(player.getName());
        super.onPlayerContact(player, level); // calls onPlayerEnter → markCheckpoint
        if (!alreadyProcessed) {
            checkedPlayers.add(player.getName());
            level.protectCollectedCoins(player);
        }
    }
}
