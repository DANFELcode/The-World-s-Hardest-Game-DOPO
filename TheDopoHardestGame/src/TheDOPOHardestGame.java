package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal que controla el flujo del juego, gestiona el modo de juego
 * seleccionado y actúa como intermediario con la capa de presentación.
 */
public class TheDOPOHardestGame {

    private Level currentLevel;
    private GameMode currentGameMode;
    private boolean isPaused;

    public TheDOPOHardestGame() {
        this.currentGameMode = GameMode.PLAYER;
        this.isPaused = false;
    }

    /**
     * Configura el modo de juego usando el enum interno.
     */
    public void setGameMode(GameMode mode) {
        this.currentGameMode = mode;
    }

    /**
     * Inicia el nivel delegando la creación y configuración a la clase Level.
     */
    public void startLevel(String levelPath) {
        this.currentLevel = new Level(this.currentGameMode, levelPath);
    }

    /**
     * Actualiza el estado lógico del juego (movimientos, colisiones, tiempo).
     * Este método debe ser llamado constantemente por el Game Loop de la
     * presentación.
     */
    public void update() {
        if (!isPaused && currentLevel != null) {
            currentLevel.updateLevelState();
        }
    }

    /**
     * Intermediario para que la interfaz gráfica mueva a un jugador específico.
     * 
     * @param playerIndex Índice del jugador a mover.
     * @param dx          Dirección en el eje X (-1, 0, 1).
     * @param dy          Dirección en el eje Y (-1, 0, 1).
     */
    public void movePlayer(int playerIndex, double dx, double dy) {
        if (!isPaused && currentLevel != null) {
            List<Player> players = currentLevel.getPlayers();
            if (playerIndex >= 0 && playerIndex < players.size()) {
                // El jugador valida contra el mapa si el movimiento es posible
                players.get(playerIndex).move(dx, dy, currentLevel);
            }
        }
    }

    /**
     * Consulta si el nivel actual ha sido superado con éxito.
     * Útil para que la GUI decida si mostrar la pantalla de victoria.
     */
    public boolean isLevelComplete() {
        return currentLevel != null && currentLevel.isLevelComplete();
    }

    /**
     * Retorna la lista de jugadores delegando la consulta al nivel.
     * Evita la redundancia de datos y respeta la jerarquía.
     */
    public List<Player> getPlayers() {
        if (currentLevel != null) {
            return currentLevel.getPlayers();
        }
        return new ArrayList<>();
    }

    public void togglePause() {
        this.isPaused = !this.isPaused;
    }

    public GameMode getGameMode() {
        return currentGameMode;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Enum interno que define las modalidades de juego.
     * Aprovecha las ventajas de Java para encapsular la configuración inicial.
     */
    public enum GameMode {
        PLAYER(1, false),
        PvsP(2, false),
        PvsM(1, true);

        private final int playerCount;
        private final boolean hasMachine;

        GameMode(int playerCount, boolean hasMachine) {
            this.playerCount = playerCount;
            this.hasMachine = hasMachine;
        }

        public int getPlayerCount() {
            return playerCount;
        }

        public boolean hasMachine() {
            return hasMachine;
        }
    }
}