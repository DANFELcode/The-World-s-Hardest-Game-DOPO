package domain;

public class GreenPlayer extends Player {

    private boolean absorbedFirstHit;

    public GreenPlayer(String name, double x, double y) {
        super(name, x, y, 20.0, 20.0, 1.0);
        this.absorbedFirstHit = false;
    }

    @Override
    public void die() {
        if (!absorbedFirstHit) {
            absorbedFirstHit = true;
            this.speed *= 0.7;
        } else {
            super.die();
        }
    }

    public void restoreShield() {
        absorbedFirstHit = false;
        this.speed = 1.0;
    }

    @Override
    public void changeSkin(String newSkin) {}
}
