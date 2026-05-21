package presentation;

import domain.GameMode;
import domain.TheDOPOHardestGame;
import dto.DrawCommand;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.*;

/**
 * Game view: the board, the HUD (level / coins / deaths / time), the MENU
 * button and keyboard input for both players. Driven each tick by the
 * GameLoop through {@link #update()} and {@link #refresh()}.
 */
public class JuegoPanel extends JPanel {

    private final TheDOPOHardestGame juego;
    private final MainView host;

    private final BoardPanel tablero = new BoardPanel();
    private final JButton menu = new JButton("MENU");
    private final JLabel niveles = new JLabel("Nivel: 1/2");
    private final JLabel muertes = new JLabel("MUERTES: 0");
    private final JLabel monedas = new JLabel("Monedas: 0/0");
    private final JLabel tiempo = new JLabel("Tiempo: 60");

    private final Set<Integer> keysDownPlayer1 = new HashSet<>();
    private final Set<Integer> keysDownPlayer2 = new HashSet<>();

    private final int[] prevDeaths = {0, 0};
    private final List<DrawCommand> prevPlayerCmds = new ArrayList<>();

    /** Arrow keys drive player 2; everything else drives player 1. */
    private static final Set<Integer> PVP_KEYS = new HashSet<>(Arrays.asList(
        KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT));

    /**
     * @param juego the game facade
     * @param host the main window, used for navigation and loop control
     */
    public JuegoPanel(TheDOPOHardestGame juego, MainView host) {
        super(new BorderLayout());
        this.juego = juego;
        this.host = host;
        construirUI();
        wireEventos();
    }

    private void construirUI() {
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(Color.BLACK);
        panelInfo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        menu.setForeground(Color.WHITE);
        menu.setBackground(Color.BLACK);
        menu.setFont(new Font("Arial", Font.BOLD, 14));
        menu.setBorderPainted(false);

        for (JLabel l : new JLabel[]{niveles, muertes, monedas, tiempo}) {
            l.setForeground(Color.WHITE);
            l.setFont(new Font("Arial", Font.BOLD, 14));
        }

        JPanel centerInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        centerInfo.setBackground(Color.BLACK);
        centerInfo.add(niveles);
        centerInfo.add(monedas);

        panelInfo.add(menu, BorderLayout.WEST);
        panelInfo.add(centerInfo, BorderLayout.CENTER);
        panelInfo.add(muertes, BorderLayout.EAST);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(Color.BLACK);
        panelSur.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel labelSuaMunar = new JLabel("SUA-MUNAR");
        labelSuaMunar.setForeground(Color.WHITE);
        labelSuaMunar.setFont(new Font("Arial", Font.BOLD, 14));
        panelSur.add(labelSuaMunar, BorderLayout.WEST);

        tiempo.setHorizontalAlignment(SwingConstants.CENTER);
        panelSur.add(tiempo, BorderLayout.CENTER);

        add(panelInfo, BorderLayout.NORTH);
        add(tablero, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);
    }

    private void wireEventos() {
        menu.addActionListener(e -> {
            host.detenerLoop();
            host.mostrarInicio();
        });

        tablero.setFocusable(true);
        tablero.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    juego.togglePause();
                    host.onPausaCambiada();
                    return;
                }
                if (PVP_KEYS.contains(e.getKeyCode())) {
                    keysDownPlayer2.add(e.getKeyCode());
                } else {
                    keysDownPlayer1.add(e.getKeyCode());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keysDownPlayer2.remove(e.getKeyCode());
                keysDownPlayer1.remove(e.getKeyCode());
            }
        });
    }

    /** Processes queued input and advances the domain one tick. Called by GameLoop. */
    public void update() {
        updatePlayer(0, keysDownPlayer1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D);
        if (juego.getGameMode() == GameMode.PvsP)
            updatePlayer(1, keysDownPlayer2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);

        // Snapshot player positions before the tick so effects appear at the death location.
        prevPlayerCmds.clear();
        for (DrawCommand cmd : juego.getDrawCommands())
            if (cmd.shape == DrawCommand.Shape.PLAYER) prevPlayerCmds.add(cmd);

        juego.update();
        tablero.tickEffects();

        // Detect player deaths and trigger a hit-ring effect at the pre-death position.
        int trackedPlayers = Math.min(prevDeaths.length, prevPlayerCmds.size());
        for (int i = 0; i < trackedPlayers; i++) {
            int deaths = juego.getPlayerDeaths(i);
            if (deaths > prevDeaths[i]) {
                DrawCommand dc = prevPlayerCmds.get(i);
                tablero.addEnemyHitEffect(dc.x + dc.width / 2, dc.y + dc.height / 2);
                prevDeaths[i] = deaths;
            }
        }

        // Drain bomb explosions and trigger the fireball effect at each bomb center.
        for (double[] pos : juego.drainExplosions())
            tablero.addExplosionEffect((int) pos[0], (int) pos[1]);
    }

    private void updatePlayer(int index, Set<Integer> keys, int up, int down, int left, int right) {
        double dx = 0, dy = 0;
        if (keys.contains(up))    dy -= 1;
        if (keys.contains(down))  dy += 1;
        if (keys.contains(left))  dx -= 1;
        if (keys.contains(right)) dx += 1;
        if (dx != 0 || dy != 0) juego.movePlayer(index, dx, dy);
    }

    /** Updates the HUD, handles win/lose transitions, and repaints the board. Called by GameLoop. */
    public void refresh() {
        if (juego.getCurrentLevel() != null) {
            GameMode mode = juego.getGameMode();
            boolean twoPlayers = mode == GameMode.PvsP || mode == GameMode.PvsM;
            String label2 = mode == GameMode.PvsM ? "MAQ" : "P2";
            if (twoPlayers) {
                muertes.setText("P1: " + juego.getPlayerDeaths(0) + " muertes  |  " + label2 + ": " + juego.getPlayerDeaths(1) + " muertes");
            } else {
                muertes.setText("MUERTES: " + juego.getPlayerDeaths(0));
            }
            if (twoPlayers) {
                monedas.setText("P1: " + juego.getPlayerCoins(0) + "/" + juego.getPlayerTotalCoins(0)
                    + "  |  " + label2 + ": " + juego.getPlayerCoins(1) + "/" + juego.getPlayerTotalCoins(1));
            } else {
                monedas.setText("Monedas: " + juego.getPlayerCoins(0) + "/" + juego.getPlayerTotalCoins(0));
            }
            niveles.setText("Nivel: " + juego.getLevelNumber());
            tiempo.setText(twoPlayers ? "" : "Tiempo: " + String.format("%.0f", juego.getRemainingTime()));

            if (juego.isLevelComplete()) {
                boolean hasNext = juego.hasNextLevel();
                juego.advanceLevel();
                if (!hasNext) {
                    host.detenerLoop();
                    clearKeys();
                    String msg = twoPlayers ? buildResultadoPvsP() : "¡HAS GANADO EL JUEGO!";
                    mostrarDialogoFin(msg);
                }
            } else if (juego.isGameOver()) {
                host.detenerLoop();
                clearKeys();
                mostrarDialogoFin("¡Tiempo agotado!");
            }
        }
        tablero.setPaused(juego.isPaused());
        tablero.updateGraphics(juego.getDrawCommands(), juego.getBackgroundColor());
    }

    private String buildResultadoPvsP() {
        Map<String, Integer> levelsWon = juego.getLevelsWon();
        int wonP1 = levelsWon.getOrDefault("Player1", 0);
        int wonP2 = levelsWon.getOrDefault("Player2", 0);
        String ganador = wonP1 > wonP2 ? "Player1" : wonP2 > wonP1 ? "Player2" : "Empate";
        return "=== RESULTADO FINAL ===\n"
            + "Player1 — Niveles ganados: " + wonP1
            + "  |  Muertes: " + juego.getPlayerDeaths(0)
            + "  |  Monedas: " + juego.getPlayerLifetimeCoins(0) + "\n"
            + "Player2 — Niveles ganados: " + wonP2
            + "  |  Muertes: " + juego.getPlayerDeaths(1)
            + "  |  Monedas: " + juego.getPlayerLifetimeCoins(1) + "\n\n"
            + (ganador.equals("Empate") ? "¡EMPATE!" : "¡Ganó " + ganador + "!");
    }

    private void mostrarDialogoFin(String mensaje) {
        String[] opciones = {"Nueva Partida", "Menú", "Salir"};
        int opcion = JOptionPane.showOptionDialog(
            this, mensaje, "Fin del juego",
            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
            null, opciones, opciones[0]);
        if (opcion == 0) {
            juego.startGame(1);
            host.iniciarJuego();
        } else if (opcion == 2) {
            System.exit(0);
        } else {
            host.mostrarInicio();
        }
    }

    /** Enables or disables death/explosion visual effects. */
    public void setEffectsEnabled(boolean enabled) { tablero.setEffectsEnabled(enabled); }

    /** @return whether death/explosion visual effects are enabled. */
    public boolean isEffectsEnabled() { return tablero.isEffectsEnabled(); }

    /** Clears all queued key presses for both players. */
    public void clearKeys() {
        keysDownPlayer1.clear();
        keysDownPlayer2.clear();
    }

    /**
     * Requests keyboard focus on the board so movement keys are captured.
     * Also clears any stale key presses: if the player leaves the game while
     * holding a key, its keyReleased never fires and the key stays "stuck".
     */
    public void requestBoardFocus() {
        clearKeys();
        tablero.requestFocusInWindow();
    }
}
