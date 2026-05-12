package domain;

import java.awt.Color;

public class RedPlayer extends Player {
    
    private static final double ORIGINAL_SPEED = 1.0 * UNIT;
    private static final double ORIGINAL_WIDTH = 20.0;
    private static final double ORIGINAL_HEIGHT = 20.0;
    private static final Color ORIGINAL_COLOR = Color.RED;

    private Color currentColor = ORIGINAL_COLOR;
    
    private boolean hasGreenSkin = false;
    private boolean isWeakened = false;

    private long lastHitTime = 0; 
    private static final long INVULNERABILITY_TIME = 1500;

    public RedPlayer(String name, double x, double y) {
        super(name, x, y, 20.0, 20.0, ORIGINAL_SPEED);
    }

    @Override
    public void changeSkin(String newSkin) {
        if (newSkin.equalsIgnoreCase("Blue")) {
            this.speed = 1.5 * UNIT;
            this.width = 30.0;
            this.height = 30.0;
            this.currentColor = Color.BLUE;
            this.hasGreenSkin = false; 
            
        } else if (newSkin.equalsIgnoreCase("Green")) {
            this.speed = ORIGINAL_SPEED; 
            this.width = ORIGINAL_WIDTH;
            this.height = ORIGINAL_HEIGHT;
            this.currentColor = new Color(0, 150, 0); 
            this.hasGreenSkin = true; 
            this.isWeakened = false;  
        }
    }

    public void restoreSkin() {
        this.speed = ORIGINAL_SPEED;
        this.width = ORIGINAL_WIDTH;
        this.height = ORIGINAL_HEIGHT;
        this.currentColor = ORIGINAL_COLOR;
        
        this.hasGreenSkin = false;
        this.isWeakened = false;
    }

    @Override
    public void die() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastHitTime < INVULNERABILITY_TIME) {
            return;
        }

        if (hasGreenSkin && !isWeakened) {
            this.isWeakened = true;
            this.speed = ORIGINAL_SPEED * 0.5; 
            this.currentColor = new Color(144, 238, 144); 
            
            this.lastHitTime = currentTime; 
        } else {
            restoreSkin();
            super.die();
            
            this.lastHitTime = currentTime; 
        }
    }

    @Override
    public Color getDisplayColor() { 
        return currentColor; 
    }
}