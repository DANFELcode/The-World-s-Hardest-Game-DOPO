package domain;

/**
 * Green player with damage absorption: takes one hit before dying (becomes weakened first). <br>
 * Uses GreenSkin which slows the player to 0.7x speed after the first hit. <br>
 * <b>Inv:</b> speed > 0 and width > 0 and height > 0
 */
public class GreenPlayer extends Player {

    public GreenPlayer(String name, double x, double y) {
        super(name, x, y, 20.0, 20.0, 1.0 * UNIT);
    }

    @Override
    protected SkinBehavior createDefaultSkin() {
        return new GreenSkin();
    }

    @Override
    public String getTypeName() { return "green"; }
}
