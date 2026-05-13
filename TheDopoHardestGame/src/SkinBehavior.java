package domain;

import java.awt.Color;
import java.io.Serializable;

public interface SkinBehavior extends Serializable {
    void apply(Player player);
    void onHit(Player player);
    Color getDisplayColor();
}
