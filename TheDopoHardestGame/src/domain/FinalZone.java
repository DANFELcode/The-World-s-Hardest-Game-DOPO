package domain;

import java.awt.Color;

/**
 * Represents the final zone the player must reach after collecting all coins. <br>
 * <b>(x, y, width, height, visited)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public class FinalZone extends Zone {
	
	private String ownerName;

    public FinalZone(double x, double y, double width, double height, String ownerName) {
        super(x, y, width, height);
        this.ownerName = ownerName;
    }

    @Override
    public Color getDisplayColor() { return GameConstants.COLOR_FINAL_ZONE; }
    
    @Override
    public void onPlayerContact(Player player, Level level) {
    	super.onPlayerContact(player, level);
    	if (player.getName().equals(ownerName)
    			&& level.isCoinsCollectedBy(player)
    			&& !level.hasWinner()
    		) {
    		level.setWinner(player);
    	}
    	
    			
    	

    	
    }
}