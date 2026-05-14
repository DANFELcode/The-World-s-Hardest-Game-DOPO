package domain;

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
