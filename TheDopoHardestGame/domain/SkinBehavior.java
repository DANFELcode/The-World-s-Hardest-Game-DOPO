package domain;

import java.awt.Color;
import java.io.Serializable;

public interface SkinBehavior extends Serializable {
    void apply(Player player);
    void onHit(Player player, Level level);
    Color getDisplayColor();

    /** Returns the type identifier for this skin (e.g. "red", "blue", "green"). */
    String getSkinType();

    /** Factory: builds a fresh skin instance for the given type identifier. */
    //retorna un tipo de skin dependiendo de el tipo de skin que tenga
    static SkinBehavior of(String type) {
        if (type == null) return new DefaultSkin();
        switch (type.toLowerCase()) {
            case "blue":  return new BlueSkin();
            case "green": return new GreenSkin();
            default:      return new DefaultSkin();
        }
    }
}
