package domain;

import java.awt.geom.Rectangle2D;

public interface Interactable {
    void onPlayerContact(Player player, Level level);
    Rectangle2D getAreaColision();
    default boolean shouldRemove() { return false; }
}
