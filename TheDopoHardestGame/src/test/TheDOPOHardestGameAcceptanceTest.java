package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Bomb;
import domain.Coin;
import domain.Enemy;
import domain.GameDataAccess;
import domain.GameMode;
import domain.Interactable;
import domain.Level;
import domain.Player;
import domain.StaticElement;
import domain.Zone;
import domain.TheDOPOHardestGame;
import presentation.BoardPanel;
import javax.swing.JFrame;

/**
 * Pruebas de integración visuales simulando la interacción real.
 */
public class TheDOPOHardestGameAcceptanceTest {

    private GameDataAccess dataAccess;
    private TheDOPOHardestGame game;
    private Level level;
    private JFrame frame;
    private BoardPanel panel;

    @BeforeEach
    public void setUp() {
        dataAccess = GameDataAccess.getInstance();
    }

    @AfterEach
    public void tearDown() {
        if (frame != null) {
            frame.dispose(); // Cierra la ventana tras la prueba
        }
    }

    /**
     * Levanta una ventana gráfica, importa el nivel en el Engine y crea los jugadores.
     */
    private void loadAndVisualize(String filename, GameMode mode) throws Exception {
        File file = new File("resources/levels/AcceptanceTest/" + filename);

        game = new TheDOPOHardestGame();
        game.setGameMode(mode);
        game.importarNivel(file); // Carga el currentLevel y crea los jugadores en el Engine
        level = game.getCurrentLevel();

        // Crear la Ventana de Observación para la simulación
        frame = new JFrame("DOPO Visual Test - " + filename);
        panel = new BoardPanel();
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Thread.sleep(500); // Pausa inicial para visualizar la ventana
    }

    /**
     * Mueve al jugador frame a frame hacia el objetivo.
     */
    private void walkTo(Player player, Interactable target) throws Exception {
        int initialDeaths = player.getDeaths();
        while (true) {
            if (player.getDeaths() > initialDeaths) break; // Termina si el jugador muere
            if (target.shouldRemove()) break; // Termina si el objetivo desaparece
            if (Coin.class.isAssignableFrom(target.getClass()) && ((Coin)target).isCollected()) break; // Termina si se recoge la moneda

            double targetX = target.getAreaColision().getCenterX() - player.getWidth() / 2.0;
            double targetY = target.getAreaColision().getCenterY() - player.getHeight() / 2.0;

            if (player.getAreaColision().intersects(target.getAreaColision())) {
                break; // Colisión detectada
            }

            double dist = Math.hypot(targetX - player.getX(), targetY - player.getY());
            if (dist < 5.0) {
                break; // Colisión inminente
            }

            double dx = targetX - player.getX();
            double dy = targetY - player.getY();

            // Movimiento del jugador
            player.move(dx, dy, level);
            level.updateLevel();
            panel.updateGraphics(game.getDrawCommands(), game.getBackgroundColor()); // Repintar pantalla

            Thread.sleep(16); // 60 FPS
        }

        // Procesar el último frame de la colisión
        level.updateLevel();
        panel.updateGraphics(game.getDrawCommands(), game.getBackgroundColor());
        Thread.sleep(800); // Pausa visual
    }

    /**
     * Mueve al jugador hacia una coordenada específica vacía.
     */
    private void walkTo(Player player, double targetX, double targetY) throws Exception {
        double dist = Math.hypot(targetX - player.getX(), targetY - player.getY());
        while (dist > 5.0) {
            double dx = targetX - player.getX();
            double dy = targetY - player.getY();
            player.move(dx, dy, level);
            level.updateLevel();
            panel.updateGraphics(game.getDrawCommands(), game.getBackgroundColor());
            Thread.sleep(16);
            double newDist = Math.hypot(targetX - player.getX(), targetY - player.getY());
            if (newDist >= dist) break;
            dist = newDist;
        }
        Thread.sleep(800);
    }

    @Test
    public void testMecanicaDeBombas() throws Exception {
        loadAndVisualize("nivel_test_bomba.txt", GameMode.PLAYER);
        Player player = level.getPlayers().get(0);

        Bomb bomb1 = (Bomb) level.getStaticElements().stream().filter(e -> Bomb.class.isAssignableFrom(e.getClass())).toArray()[0];
        Bomb bomb2 = (Bomb) level.getStaticElements().stream().filter(e -> Bomb.class.isAssignableFrom(e.getClass())).toArray()[1];
        Enemy enemy = level.getEnemies().get(0);

        // --- PRUEBA A: Player camina hacia la bomba ---
        double initialX = player.getX();
        int initialDeaths = player.getDeaths();

        walkTo(player, bomb1);

        assertEquals(initialDeaths + 1, player.getDeaths(), "El jugador debió morir al tocar la bomba");
        assertEquals(initialX, player.getX(), "El jugador debió regresar al respawn original tras la muerte");

        // --- PRUEBA B: El enemigo se acerca a la segunda bomba ---
        // El enemigo camina hacia la segunda bomba para activarla.
        double startX = enemy.getX(), startY = enemy.getY();
        double endX = bomb2.getX(), endY = bomb2.getY();
        int steps = 45;
        for (int i = 1; i <= steps; i++) {
            double t = i / (double) steps;
            enemy.setPosition(startX + (endX - startX) * t, startY + (endY - startY) * t);
            level.updateLevel();
            panel.updateGraphics(game.getDrawCommands(), game.getBackgroundColor());
            Thread.sleep(16);
            if (enemy.isDead() || !level.getEnemies().contains(enemy)) break;
        }
        Thread.sleep(800);

        assertTrue(enemy.isDead() || !level.getEnemies().contains(enemy), "El enemigo debió morir al chocar con la bomba");
    }

    @Test
    public void testMecanicaMonedasYSkinVerde() throws Exception {
        loadAndVisualize("nivel_test_monedas.txt", GameMode.PLAYER);
        Player player = level.getPlayers().get(0);

        Coin yellowCoin = level.getCoins().stream().filter(c -> "yellow".equals(c.getCoinType())).findFirst().get();
        Coin greenCoin = level.getCoins().stream().filter(c -> "green".equals(c.getCoinType())).findFirst().get();
        Enemy trap = level.getEnemies().get(0);

        // --- PRUEBA A: Recolección normal (Amarilla) ---
        int initialCoins = level.getCoinsCollectedCountBy(player);
        walkTo(player, yellowCoin);

        assertEquals(initialCoins + 1, level.getCoinsCollectedCountBy(player), "El jugador debió sumar 1 moneda");
        assertTrue(yellowCoin.isCollected(), "La moneda amarilla debió recolectarse visualmente");

        // --- PRUEBA B: Recolección moneda verde (Skin) ---
        walkTo(player, greenCoin);

        assertTrue(greenCoin.isCollected(), "La moneda verde debió recolectarse");
        assertEquals(domain.GameConstants.COLOR_GREEN_NORMAL, player.getDisplayColor(), "El Player debe brillar con Skin Verde");

        // --- PRUEBA C: Sobrevivir trampa teniendo el skin verde ---
        int deathsBeforeHit = player.getDeaths();
        double speedBeforeHit = player.getSpeed();

        walkTo(player, trap);

        assertEquals(deathsBeforeHit, player.getDeaths(), "El jugador NO debió morir (el skin lo protegió)");
        assertEquals(domain.GameConstants.COLOR_GREEN_WEAKENED, player.getDisplayColor(), "El skin verde debió palidecer por el golpe absorbido");
        assertTrue(player.getSpeed() < speedBeforeHit, "El jugador debió ser ralentizado visiblemente");

        // Caminamos a una zona vacía para mostrar el cambio visual
        walkTo(player, player.getX() + 50, player.getY() - 50);
    }

    @Test
    public void testMecanicaLifesource() throws Exception {
        loadAndVisualize("nivel_test_lifesource.txt", GameMode.PLAYER);
        Player player = level.getPlayers().get(0);

        StaticElement lifeSource = level.getStaticElements().stream()
            .filter(e -> e.getClass().getSimpleName().equals("LifeSource"))
            .findFirst().get();

        int initialLives = player.getExtraLives();

        walkTo(player, lifeSource);

        assertEquals(initialLives + 1, player.getExtraLives(), "El jugador debió registrar internamente una Vida Extra");

        // --- PRUEBA B: Resistir golpe del enemigo usando la Vida Extra ---
        Enemy enemy = level.getEnemies().get(0);
        int deathsBeforeEnemy = player.getDeaths();

        walkTo(player, enemy);

        assertEquals(deathsBeforeEnemy, player.getDeaths(), "El jugador NO debió morir gracias al LifeSource");
        assertEquals(initialLives, player.getExtraLives(), "La vida extra debió consumirse al absorber el impacto");

        // Caminamos a una zona vacía para confirmar que el jugador sobrevivió
        walkTo(player, player.getX() + 50, player.getY() - 50);
    }

    @Test
    public void testMecanicaZonasYWinCondition() throws Exception {
        loadAndVisualize("nivel_test_zonas.txt", GameMode.PLAYER);
        Player player = level.getPlayers().get(0);

        Zone intermediateZone = level.getZones().values().stream()
            .filter(z -> z.getClass().getSimpleName().equals("IntermediateZone")).findFirst().get();
        Zone finalZone = level.getZones().values().stream()
            .filter(z -> z.getClass().getSimpleName().equals("FinalZone")).findFirst().get();

        // --- PRUEBA A: Intermediate Zone ---
        walkTo(player, intermediateZone);

        assertTrue(player.hasCheckpoint(), "El jugador registró visual e internamente el Checkpoint");
        double expectedSpawnX = intermediateZone.getX() + (intermediateZone.getWidth() / 2.0);
        double expectedSpawnY = intermediateZone.getY() + (intermediateZone.getHeight() / 2.0);
        assertEquals(expectedSpawnX, player.getSpawnX(), "El SpawnX debió anclarse a la zona intermedia");

        // --- PRUEBA B: Suicidio para probar el Respawn ---
        Enemy enemy = level.getEnemies().get(0);
        int initialDeaths = player.getDeaths();

        walkTo(player, enemy); // Caminar hacia el enemigo

        assertEquals(initialDeaths + 1, player.getDeaths(), "El jugador debió morir a manos del enemigo");
        assertEquals(expectedSpawnX, player.getX(), "Al morir, el jugador debió reaparecer en la Zona Intermedia, NO en la inicial");
        assertEquals(expectedSpawnY, player.getY(), "Al morir, el jugador debió reaparecer en la Zona Intermedia, NO en la inicial");

        // --- PRUEBA C: Final Zone (Victoria) ---
        walkTo(player, finalZone);

        assertTrue(level.isLevelComplete(), "El Nivel completado debe ser activado al tocar la Final Zone");
        assertEquals(player, level.getWinner(), "Coronado como ganador del nivel");

        // Pausa para visualizar la victoria
        Thread.sleep(1500);
    }

    @Test
    public void testMecanica1vs1PropiedadMonedas() throws Exception {
        loadAndVisualize("nivel_test_pvsp.txt", GameMode.PvsP);

        Player player1 = level.getPlayers().stream().filter(p -> "Player1".equals(p.getName())).findFirst().get();
        Player player2 = level.getPlayers().stream().filter(p -> "Player2".equals(p.getName())).findFirst().get();

        // Asignar colores para diferenciar a los jugadores
        player1.setBorderColor(java.awt.Color.BLUE);
        player2.setBorderColor(java.awt.Color.RED);

        Coin p1Coin = level.getCoins().stream().filter(c -> "Player1".equals(c.getOwnerName())).findFirst().get();
        Coin p2Coin = level.getCoins().stream().filter(c -> "Player2".equals(c.getOwnerName())).findFirst().get();

        // --- PRUEBA A: Intentar robar la moneda enemiga ---
        walkTo(player1, p2Coin);
        assertFalse(p2Coin.isCollected(), "La moneda de Player2 rechazó ser recogida por Player1");

        // --- PRUEBA B: Ir por la moneda propia ---
        walkTo(player1, p1Coin);
        assertTrue(p1Coin.isCollected(), "La moneda de Player1 fue recogida exitosamente");
    }

    @Test
    public void testPausarJuego() throws Exception {
        loadAndVisualize("nivel_test_monedas.txt", GameMode.PLAYER);
        Player player = level.getPlayers().get(0);
        
        // Caminamos un poco antes de pausar
        walkTo(player, player.getX() + 50, player.getY());
        
        // Pausar
        game.togglePause();
        panel.setPaused(game.isPaused()); // Simular la interfaz de pausa
        panel.updateGraphics(game.getDrawCommands(), game.getBackgroundColor());
        
        double xBefore = player.getX();
        int timeBefore = level.getGameTime();
        
        // Pausa para visualizar el estado pausado
        Thread.sleep(1500); 
        
        // Intentar movimiento durante la pausa
        game.movePlayer(0, 10, 0);
        level.updateLevel();
        
        assertEquals(xBefore, player.getX(), "El jugador no debe moverse estando en pausa");
        assertEquals(timeBefore, level.getGameTime(), "El tiempo no debe avanzar estando en pausa");
        
        // Despausar
        game.togglePause();
        panel.setPaused(game.isPaused());
        
        // Validar que se reanuda
        walkTo(player, player.getX() + 50, player.getY());
        assertTrue(player.getX() > xBefore, "El jugador debe moverse después de despausar");
    }


    @Test
    public void testVictoriaYMensaje() throws Exception {
        loadAndVisualize("nivel_test_zonas.txt", GameMode.PLAYER);
        Player player = level.getPlayers().get(0);
        Zone finalZone = level.getZones().values().stream()
            .filter(z -> z.getClass().getSimpleName().equals("FinalZone")).findFirst().get();
            
        // Llegar a la meta
        walkTo(player, finalZone);
        assertTrue(level.isLevelComplete(), "El nivel debe completarse al llegar a la meta");
        
        // Mostrar mensaje de victoria y cerrarlo automáticamente
        String[] opciones = {"Nueva Partida", "Menú", "Salir"};
        
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(3000); // Esperar antes de cerrar el diálogo
                java.awt.Window[] windows = java.awt.Window.getWindows();
                for (java.awt.Window w : windows) {
                    if (javax.swing.JDialog.class.isAssignableFrom(w.getClass())) {
                        w.dispose(); // Cerrar ventana automáticamente
                    }
                }
            } catch (Exception e) {}
        });
        t.start();
        
        javax.swing.JOptionPane.showOptionDialog(frame, 
            "¡HAS GANADO EL JUEGO!", "Victoria", 
            javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.INFORMATION_MESSAGE, 
            null, opciones, opciones[0]);
            
        // Verificación final
        assertTrue(true);
    }
}
