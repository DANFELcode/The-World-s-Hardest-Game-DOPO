package domain;

import dto.DrawCommand; //Objeto de transferencia entre dominio y presentación

import java.awt.Color;

/**
 * Represents a player in the game. <br>
 * <b>(name, deaths, spawnX, spawnY, hasCheckpoint, currentSkin, lastSkinType, extraLives, borderColor, strategy, coinsCollected)</b> <br>
 * <b>Inv:</b> deaths >= 0, extraLives >= 0, coinsCollected >= 0, currentSkin != null,
 * borderColor != null, lastSkinType is null or a valid skin type ("red", "blue", "green"),
 * hasCheckpoint implies lastSkinType may be preserved on death, strategy is null (human) or non-null (AI)
 */
public abstract class Player extends MovableElement implements Drawable, Interactable {
    protected String name;
    protected int deaths;
    protected double spawnX;
    protected double spawnY;
    // atributo para saber si el jugador ha llegado a la zona intermedia
    // para saber si aparece en el checkpoint original o en la zona intermedia   
    private boolean hasCheckpoint;
    protected SkinBehavior currentSkin;
    private String lastSkinType; // ultima skin recolectada, por defecto se inicia null asi que no hay necesidad de ponerla en el constructor
    private String checkpointSkinType; // snapshot de la ultima skin al cruzar la zona intermedia
    protected int extraLives = 0; // se inicializa antes por seguridad
    protected Color borderColor = Color.BLACK;
    private GameStrategy strategy; // null = controlado por humano
    private int coinsCollected; //monedas coleccionadas a lo largo de la partida

    public Player(String name, double x, double y, double width, double height, double speed) {
        super(x, y, width, height, speed);
        this.name = name;
        this.spawnX = x;
        this.spawnY = y;
        this.deaths = 0;
        this.hasCheckpoint = false;
        changeSkin(createDefaultSkin()); //tiene que crear la skin por defecto al crear objeto
        this.coinsCollected = 0;
    }

    protected abstract SkinBehavior createDefaultSkin(); //cada subclase crea su propia skin por defecto

    /** Factory method: creates the appropriate Player subclass for the given type identifier. */
    //Player.create existe para evitar que el código cliente 
    //(la fachada, GameDataAccess) necesite saber qué subclase concreta instanciar.
    
    public static Player create(String type, String name, double x, double y) {
        switch (type) {
            case "blue":  return new BluePlayer(name, x, y);
            case "green": return new GreenPlayer(name, x, y);
            default:      return new RedPlayer(name, x, y);
        }
    }
    
    // se deja metodo por extension futura, si alguna subclase necesita implementarlo
    /** No-op hook called when a coin is picked up. Per-level count lives in Level. */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public void collectCoin() { }

    /** Adds the given amount to the player's lifetime coin total (at level end). */
    public void addToLifetime(int amount) {
        coinsCollected += amount;
    }

    /**
     * Marks a checkpoint at the given position. Future deaths at this checkpoint
     * preserve the last collected SkinCoin's type.
     * @param x checkpoint horizontal position (new spawn point)
     * @param y checkpoint vertical position (new spawn point)
     */
    
    //actualiza punto de reaparición del jugador lo llama intermediateZone
    public void markCheckpoint(double x, double y) {
        setSpawnPoint(x, y);
        this.hasCheckpoint = true;
        this.checkpointSkinType = this.lastSkinType; // snapshot de la skin al cruzar el checkpoint
    }

    /**
     * @return true if this player has reached a checkpoint, false otherwise
     */
    public boolean hasCheckpoint() {
        return hasCheckpoint;
    }

    /**
     * Clears the checkpoint flag. Future deaths revert to default skin and clear lastSkinType.
     */
    
    // lo llama la fachada al iniciar un nuevo nivel
    public void resetCheckpoint() {
    	hasCheckpoint = false;
    	checkpointSkinType = null;
    }

    // es necesario normalizar el movimiento porque el jugador no puede avanzar mas rapido diagonalmente 
    /**
     * Moves the player in the given direction by distance = speed per tick.
     * Direction is normalized if its length exceeds 1.0. Movement is rejected if
     * the destination collides with walls or is out of bounds.
     * @param dx horizontal direction component
     * @param dy vertical direction component
     * @param level the level context for walkability checks
     */
    public void move(double dx, double dy, Level level) {
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length > 1.0) {
            dx /= length;
            dy /= length;
        }
        double nextX = getX() + (dx * getSpeed());
        double nextY = getY() + (dy * getSpeed());
        //verifica que una posición sea caminable
        if (level.isWalkable(nextX, nextY, getWidth(), getHeight())) {
        	//llama al metodo del padre
            this.setPosition(nextX, nextY);
        }
    }

    /** Single entry point for death: increments counter, repositions, restores skin, resets coins. */
    public void die(Level level) {
        this.deaths++;
        this.setPosition(spawnX, spawnY);
        if (hasCheckpoint && checkpointSkinType != null) {
            // Con checkpoint, restaura la skin que tenia AL CRUZAR el checkpoint (snapshot), no la actual.
            changeSkin(SkinBehavior.of(checkpointSkinType));
        } else {
            restoreSkin();
            lastSkinType = null;
        }
        level.onPlayerDeath(this); //notifica al nivel para que resetee todo(monedas, enemigos, etc)
    }

    /** Records the type of the last collected SkinCoin (restored on checkpoint deaths). */
    //lo llama skincoin cuando se colecciona la moneda
    public void setLastSkin(String skinType) { this.lastSkinType = skinType; }

    /**
     * Called when the player is hit by an enemy or hazard. If in a safe zone, ignored.
     * If extraLives > 0, consume one life and return. Otherwise, delegate to skin behavior.
     * @param level the level context for safe zone checks
     */
    public void onHit(Level level) {
        if (level.isInSafeZone(this)) return;
        if (extraLives > 0) {
            extraLives--;
            return;
        }
        currentSkin.onHit(this, level); //cada skin se encarga de decidir que hacer si recibe un golpe
    }

    /** Adds an extra life to the player, which absorbs one hit. */
    //llamado por LifeSource.conCollect
    public void addLife() {
        this.extraLives++;
    }

    /**
     * @return the number of extra lives this player currently has
     */
    //getter para tests
    public int getExtraLives() { return extraLives; }

    /**
     * Changes the player's active skin and applies its attributes (speed, width, height).
     * @param skin the new skin behavior to apply
     */
    public final void changeSkin(SkinBehavior skin) {
        this.currentSkin = skin; //cambia la skin por la nueva seleccionada
        skin.apply(this); //cada tipo de skin sabe como aplicarse
    }

    /** Restores the player to its original skin (the one defined by createDefaultSkin). */
    //llamada por player al morir, skincoin, fachada(al avanzar de nivel)
    public void restoreSkin() {
        changeSkin(createDefaultSkin());
    }

    /**
     * @return the display color of the current skin
     */
    //getter para toDrawCommand
    public Color getDisplayColor() {
        return currentSkin.getDisplayColor();
    }

    /**
     * @return the type identifier of the current skin (e.g. "red", "blue", "green")
     */
    public String getCurrentSkinType() {
        return currentSkin.getSkinType();
    }

    /** Returns the player's type identifier used in level files (e.g. "red", "blue", "green"). */
    //lo llama gameDataAccess al guardar la partida
    public abstract String getTypeName();

    /**
     * Converts the player to a draw command. Includes an outer border in LifeSource color if extraLives > 0.
     * @return a DrawCommand with the player's current position, size, skin color, border color, and optional outer border
     */
    @Override
    
    public DrawCommand toDrawCommand() {
        Color outer = (extraLives > 0) ? GameConstants.COLOR_LIFESOURCE : null; //define el color de outer
        return new DrawCommand(getDisplayColor(), (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight(),
                DrawCommand.Shape.PLAYER, borderColor, outer); //crea objeto de transferencia
    }

    /**
     * Sets the player's border color. Null values are ignored to preserve the current color.
     * @param color the new border color
     */
    //llamado por fachada
    public void setBorderColor(Color color) {
        if (color != null) this.borderColor = color;
    }

    /**
     * @return the player's current border color
     */
    public Color getBorderColor() { return borderColor; }

    /**
     * Updates the player's spawn point (respawn location). Used when reaching a checkpoint.
     * @param newSpawnX new spawn horizontal position
     * @param newSpawnY new spawn vertical position
     */
    public void setSpawnPoint(double newSpawnX, double newSpawnY) {
        this.spawnX = newSpawnX;
        this.spawnY = newSpawnY;
    }

    /**
     * Handles collision with another player. Both players die unless either is in a safe zone.
     * Safe zones prevent player-player kills and break respawn loops.
     * @param other the other player in the collision
     * @param level the level context for safe zone checks
     */
    @Override
    public void onPlayerContact(Player other, Level level) {
        // Zones are safe sanctuaries: no player-player kills inside any zone.
        // This also breaks the respawn-loop when both share a checkpoint.
        if (level.isInSafeZone(this) || level.isInSafeZone(other)) return; // garantiza que si cualquiera de los dos está en zona segura, no pasa nada.
        this.die(level);
        other.die(level);
    }

    /**
     * Executes the AI strategy if attached. If no strategy is set, this is a no-op (human-controlled).
     * @param level the level context for strategy execution
     */
    public void automate(Level level) {
        if (strategy != null) strategy.execute(this, level);
    }

    /**
     * Attaches a game strategy to automate this player's movement. Pass null to revert to human control.
     * @param strategy the game strategy to execute, or null for human control
     */
    public void setStrategy(GameStrategy strategy) { this.strategy = strategy; }

    /**
     * @return true if this player is controlled by AI (has a strategy), false if human-controlled
     */
    public boolean isMachine() { return strategy != null; }

    /**
     * Sets the number of deaths for this player. Used when restoring saved game state.
     * @param deaths the number of deaths
     */
    
    //lo llama fachada al cargar una partida
    public void setDeaths(int deaths) { this.deaths = deaths; }

    /**
     * Restores the lifetime coin count for this player. Used when loading a saved game.
     * @param coins the number of coins collected in lifetime
     */
    public void restoreLifetime(int coins) { this.coinsCollected = coins; }

    /**
     * @return the number of times this player has died
     */
    public int getDeaths() { return deaths; }

    /**
     * Pushes this player away from the given source point by a fixed distance.
     * Called when the player survives a bomb blast (absorbed by GreenSkin or extraLife).
     * @param srcX x-coordinate of the blast center
     * @param srcY y-coordinate of the blast center
     */
    public void knockbackFrom(double srcX, double srcY, Level level) {
        double dx = (getX() + getWidth() / 2) - srcX;
        double dy = (getY() + getHeight() / 2) - srcY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 1) { dx = 1; dy = 0; dist = 1; }
        double force = UNIT * 8;
        double newX = getX() + (dx / dist) * force;
        double newY = getY() + (dy / dist) * force;
        if (level.isWalkable(newX, newY, getWidth(), getHeight())) {
            setPosition(newX, newY);
        }
    }

    /**
     * @return the player's name identifier
     */
    public String getName() { return name; }

    /**
     * @return the player's spawn point horizontal position
     */
    
    public double getSpawnX() { return spawnX; }

    /**
     * @return the player's spawn point vertical position
     */
    public double getSpawnY() { return spawnY; }

    /**
     * @return the total number of coins collected by this player in this session (lifetime)
     */
    public int getCoinsCollected() { return coinsCollected; }
    
}
