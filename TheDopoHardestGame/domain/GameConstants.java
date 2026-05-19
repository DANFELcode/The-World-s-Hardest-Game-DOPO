package domain;

import java.awt.Color;

/**
 * Shared domain constants: colors and sizes used across multiple domain classes.
 */
public final class GameConstants {

    private GameConstants() { }

    // Element colors
    public static final Color COLOR_LIFESOURCE     = new Color(255, 105, 180);
    public static final Color COLOR_BOMB           = new Color(160, 32,  240);
    public static final Color COLOR_COIN           = new Color(218, 165,  32);
    public static final Color COLOR_ENEMY          = new Color( 40,  60, 200);
    public static final Color COLOR_INITIAL_ZONE   = new Color(170, 240, 170);
    public static final Color COLOR_FINAL_ZONE     = new Color( 60, 160,  60);
    public static final Color COLOR_INTERMEDIATE_ZONE = new Color(144, 238, 144);
    public static final Color COLOR_GREEN_NORMAL   = new Color(  0, 150,   0);
    public static final Color COLOR_GREEN_WEAKENED = new Color(144, 238, 144);
    public static final Color COLOR_BOARD          = new Color(180, 181, 254);

    // Sizes
    public static final double MIN_PLAYER_SIZE = 20.0;
}
