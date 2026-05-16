package domain;

import java.awt.Color;

/**
 * Represents the starting zone of the level. Players spawn here at the start or after dying. <br>
 * <b>(x, y, width, height)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public class InitialZone extends Zone {
	private String ownerName;

    @Override
    public Color getDisplayColor() { return new Color(170, 240, 170); }


    
    @Override
    public void onPlayerContact(Player player, Level level) {
    	super.onPlayerContact(player, level);
    	if (player.getName().equals(ownerName)) {
    		player.setSpawnPoint(x, y);
    	}
    }


    /**
     * Creates an initial zone at the given position and size.
     * @param x horizontal position
     * @param y vertical position
     * @param width zone width, must be greater than 0
     * @param height zone height, must be greater than 0
     */
    public InitialZone(double x, double y, double width, double height, String ownerName) {
        super(x, y, width, height);
        this.ownerName = ownerName;
    }
}