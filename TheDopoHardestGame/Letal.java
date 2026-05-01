package domain;

/**
 * Represents an element that can destroy a player on contact. <br>
 */
public interface Letal {

    /**
     * Defines the effect on the player when contact occurs.
     * @param player the player that made contact with the lethal element
     */
    void onDestroy(Jugador player);
}
