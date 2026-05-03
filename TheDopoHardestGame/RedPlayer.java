package domain;

/**
 * Represents the standard player with normal speed and size. <br>
 * <b>(name, x, y, width=20.0, height=20.0, speed=1.0)</b> <br>
 * <b>Inv:</b> speed > 0 and width > 0 and height > 0
 */
public class RedPlayer extends Player {
	private static final double ORIGINAL_SPEED = 1.0;
	private static final double ORIGINAL_WIDTH = 20.0;
	private static final double ORIGINAL_HEIGHT = 20.0;
	

    /**
     * Creates a red player at the given position with default size (20x20) and speed (1.0).
     * @param name player name
     * @param x initial horizontal position
     * @param y initial vertical position
     */
    public RedPlayer(String name, double x, double y) {
        super(name, x, y, 20.0, 20.0, 1.0);
    }

    /**
     * Changes the player's skin stats based on the collected skin coin color.
     * @param newSkin the color of the skin coin collected
     */
    @Override
    public void changeSkin(String newSkin) {
        if (newSkin.equals("Blue")) {
            this.speed = 1.5;
            this.width = 30.0;
            this.height = 30.0;
        }
    }
    
    
    public void restoreSkin() {
    	this.speed = ORIGINAL_SPEED;
    	this.width = ORIGINAL_WIDTH;
    	this.height = ORIGINAL_HEIGHT;
    	
    }
}
