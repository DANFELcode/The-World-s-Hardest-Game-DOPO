package domain;

/**
 * Represents an element that can be collected by a player. <br>
 */
public interface Recogible {

    /**
     * Defines the effect on the player when the element is collected.
     * @param player the player that collected the element
     */
    void onCollect(Jugador player);
}
