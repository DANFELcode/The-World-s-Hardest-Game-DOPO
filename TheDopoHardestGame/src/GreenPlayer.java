package domain;

/**
 * Represents the resilient player who can absorb the first hit before dying. <br>
 * <b>(name, x, y, width=20.0, height=20.0, speed=1.0, absorbedFirstHit)</b> <br>
 * <b>Inv:</b> speed > 0 and width > 0 and height > 0
 */
public class GreenPlayer extends Player {

    private boolean absorbedFirstHit;

    /**
     * Creates a green player with the shield active.
     * @param name player name
     * @param x initial horizontal position
     * @param y initial vertical position
     */
    public GreenPlayer(String name, double x, double y) {
        super(name, x, y, 20.0, 20.0, 1.0);
        this.absorbedFirstHit = false;
    }

    /**
     * On the first hit absorbs the impact and reduces speed; on the second hit dies and respawns.
     */
    @Override
    public void die() {
        if (!absorbedFirstHit) {
            absorbedFirstHit = true;
            this.speed *= 0.7;
        } else {
            super.die();
        }
    }

    /**
     * Restores the shield and the original speed at the start of a new level.
     */
    public void restoreShield() {
        absorbedFirstHit = false;
        this.speed = 1.0;
    }

    /**
     * Changes the player's skin (no effect by default).
     * @param newSkin the color of the new skin
     */
    @Override
    public void changeSkin(String newSkin) {}
}
