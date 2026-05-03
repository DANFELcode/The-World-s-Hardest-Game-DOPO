package domain;

public class Machine extends Player {

    private GameStrategy strategy;

    public Machine(String name, double x, double y, double width, double height, double speed, GameStrategy strategy) {
        super(name, x, y, width, height, speed);
        this.strategy = strategy;
    }

    @Override
    public void changeSkin(String newSkin) {}
}
