package domain;

/**
 * Representa al jugador estándar con velocidad y tamaño normales
 */
public class RedPlayer extends Player {

    public RedPlayer(String name, double x, double y) {
        super(name, x, y, 20.0, 20.0, 1.0);
    }

    @Override
    public void changeSkin(String newSkin) {
        if (newSkin.equals("Blue")) {
            this.speed = 1.5;
            this.width = 30.0;
            this.height = 30.0;
        } else if (newSkin.equals("Green")) {
            this.speed = 1.0;
        }
    }
}