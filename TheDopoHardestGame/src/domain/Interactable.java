package domain;

import java.awt.geom.Rectangle2D;

/**
 * Element that collides with a player. Implemented por Player, Enemy, StaticElement y Zone.
 */
public interface Interactable {
    void onPlayerContact(Player player, Level level);
    Rectangle2D getAreaColision();
    default boolean shouldRemove() { return false; }
}
