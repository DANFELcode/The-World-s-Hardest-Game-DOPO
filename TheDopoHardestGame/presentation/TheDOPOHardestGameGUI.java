package presentation;

import domain.TheDOPOHardestGame;
import java.awt.*;
import javax.swing.*;
import java.util.HashSet;
import java.util.Set;
import java.awt.event.*;

public class TheDOPOHardestGameGUI extends JFrame {

    private TheDOPOHardestGame juego;
    private JMenuBar menuBar;
    private JMenu opciones, archivo;
    private JMenuItem nuevaPartida, pausar, salir, reiniciar;
    private JMenuItem guardarPartida, abrirPartida, exportarNivel, importarNivel;

    private JPanel panel;
    private CardLayout cardLayout;

    private JPanel panelInicio;
    private JLabel labelTitulo;
    private JButton playGame;
    private JButton settings;

    private JPanel panelExp;
    private JLabel descripcion;
    private JButton playGame2;
    private JButton backInicio;

    private JPanel panelSeleccion;
    private JButton btnModePlayer, btnModePvsP, btnModePvsM;
    private JButton btnSkinRojo, btnSkinAzul, btnSkinVerde;
    private JPanel filaBorde1, filaBorde2;
    private JButton[] borde1Botones;
    private JButton[] borde2Botones;
    private JButton btnJugarSeleccion, btnVolverSeleccion;
    private TheDOPOHardestGame.GameMode selectedMode = TheDOPOHardestGame.GameMode.PLAYER;
    private String selectedSkin = "red";
    private Color selectedBorder1 = Color.BLACK;
    private Color selectedBorder2 = Color.WHITE;

    private static final String[] BORDER_NAMES = {"NEGRO", "BLANCO", "AMARILLO", "CYAN", "MAGENTA"};
    private static final Color[] BORDER_COLORS = {Color.BLACK, Color.WHITE, Color.YELLOW, Color.CYAN, Color.MAGENTA};

    private JPanel panelJuego;
    private BoardPanel tablero;
    private JButton menu;
    private JLabel niveles;
    private JLabel muertes;
    private JLabel monedas;
    private JLabel tiempo;

    private GameLoop gameLoop;

    private final Set<Integer> keysDownPlayer1 = new HashSet<>();
    private final Set<Integer> keysDownPlayer2 = new HashSet<>();
    

    private static final Color COLOR_FONDO        = new Color(180, 180, 220);
    private static final String PANEL_INICIO      = "inicio";
    private static final String PANEL_EXPLICACION = "explicacion";
    private static final String PANEL_SELECCION   = "seleccion";
    private static final String PANEL_JUEGO       = "juego";

    public TheDOPOHardestGameGUI() {
        super("TheDOPOHardestGame");
        juego = new TheDOPOHardestGame();

        prepareElements();
        prepareActions();
        gameLoop = new GameLoop(this);

        pack();
        this.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        TheDOPOHardestGameGUI ventana = new TheDOPOHardestGameGUI();
        ventana.setVisible(true);
    }

    public void prepareElements() {
        cardLayout = new CardLayout();
        panel = new JPanel(cardLayout);

        prepareElementsPanelInicio();
        prepareElementsPanelExp();
        prepareElementsPanelSeleccion();
        prepareElementsPanelJuego();
        prepareElementsMenuBar();

        this.add(panel);
    }

    private void prepareElementsMenuBar() {
        menuBar = new JMenuBar();

        archivo = new JMenu("Archivo");
        guardarPartida = new JMenuItem("Guardar Partida");
        abrirPartida = new JMenuItem("Abrir Partida");
        exportarNivel = new JMenuItem("Exportar Nivel");
        importarNivel = new JMenuItem("Importar Nivel");
        archivo.add(guardarPartida);
        archivo.add(abrirPartida);
        archivo.addSeparator();
        archivo.add(exportarNivel);
        archivo.add(importarNivel);

        opciones = new JMenu("Opciones");
        nuevaPartida = new JMenuItem("Nueva Partida");
        pausar = new JMenuItem("Pausar");
        reiniciar = new JMenuItem("Reiniciar");
        salir = new JMenuItem("Salir");
        opciones.add(nuevaPartida);
        opciones.add(pausar);
        opciones.add(reiniciar);
        opciones.addSeparator();
        opciones.add(salir);

        menuBar.add(archivo);
        menuBar.add(opciones);

        this.setJMenuBar(menuBar);
    }

    private void prepareElementsPanelInicio() {
        panelInicio = new JPanel(new BorderLayout());
        panelInicio.setBackground(COLOR_FONDO);

        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(COLOR_FONDO);

        labelTitulo = new JLabel("THE DOPO HARDEST GAME");
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 40));
        labelTitulo.setForeground(new Color(30, 80, 180));
        labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        panelTitulo.add(labelTitulo, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        panelBotones.setBackground(COLOR_FONDO);

        playGame = new JButton("JUGAR");
        playGame.setFont(new Font("Arial", Font.BOLD, 20));
        playGame.setForeground(Color.RED);

        settings = new JButton("CONFIGURACIONES");
        settings.setFont(new Font("Arial", Font.BOLD, 20));
        settings.setForeground(new Color(0, 150, 0));

        panelBotones.add(playGame);
        panelBotones.add(settings);

        panelInicio.add(panelTitulo, BorderLayout.CENTER);
        panelInicio.add(panelBotones, BorderLayout.SOUTH);

        panel.add(panelInicio, PANEL_INICIO);
    }

    private void prepareElementsPanelExp() {
        panelExp = new JPanel(new BorderLayout());
        panelExp.setBackground(COLOR_FONDO);

        descripcion = new JLabel("<html><div style='text-align: justify; width: 400px'>"
            + "Eres el cuadrado <font color='red'><b>rojo</b></font>. "
            + "Evita los circulos <font color='blue'><b>azules</b></font> y recolecta las "
            + "monedas <font color='#DAA520'><b>amarillas</b></font>. "
            + "Una vez recolectadas todas las monedas, ve hacia la "
            + "zona <font color='green'><b>verde</b></font> antes de que se acabe el tiempo para completar el nivel. "
            + "Algunos niveles tienen mas de una zona verde, estas zonas son check points. "
            + "</div></html>");
        descripcion.setFont(new Font("Arial", Font.PLAIN, 20));
        descripcion.setHorizontalAlignment(SwingConstants.CENTER);
        descripcion.setBorder(BorderFactory.createEmptyBorder(40, 60, 20, 60));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        panelBotones.setBackground(COLOR_FONDO);

        backInicio = new JButton("VOLVER AL MENU");
        backInicio.setFont(new Font("Arial", Font.BOLD, 20));
        backInicio.setForeground(new Color(150, 0, 200));

        playGame2 = new JButton("JUGAR");
        playGame2.setFont(new Font("Arial", Font.BOLD, 20));
        playGame2.setForeground(Color.RED);

        panelBotones.add(backInicio);
        panelBotones.add(playGame2);

        panelExp.add(descripcion, BorderLayout.CENTER);
        panelExp.add(panelBotones, BorderLayout.SOUTH);

        panel.add(panelExp, PANEL_EXPLICACION);
    }

    private void prepareElementsPanelSeleccion() {
        panelSeleccion = new JPanel(new BorderLayout());
        panelSeleccion.setBackground(COLOR_FONDO);

        JLabel titulo = new JLabel("Seleccione el modo de juego");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(new Color(30, 80, 180));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        panelSeleccion.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(4, 1, 0, 10));
        centro.setBackground(COLOR_FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

        // Fila de modos
        JPanel filaModos = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        filaModos.setBackground(COLOR_FONDO);
        JLabel labelModo = new JLabel("Modo:  ");
        labelModo.setFont(new Font("Arial", Font.BOLD, 16));
        btnModePlayer = new JButton("PLAYER");
        btnModePvsP   = new JButton("PvsP");
        btnModePvsM   = new JButton("PvsM");
        for (JButton b : new JButton[]{btnModePlayer, btnModePvsP, btnModePvsM}) {
            b.setFont(new Font("Arial", Font.BOLD, 16));
        }
        filaModos.add(labelModo);
        filaModos.add(btnModePlayer);
        filaModos.add(btnModePvsP);
        filaModos.add(btnModePvsM);

        // Fila de skins
        JPanel filaSkins = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        filaSkins.setBackground(COLOR_FONDO);
        JLabel labelSkin = new JLabel("Skin:  ");
        labelSkin.setFont(new Font("Arial", Font.BOLD, 16));
        btnSkinRojo  = new JButton("ROJO");
        btnSkinAzul  = new JButton("AZUL");
        btnSkinVerde = new JButton("VERDE");
        btnSkinRojo.setForeground(Color.RED);
        btnSkinAzul.setForeground(Color.BLUE);
        btnSkinVerde.setForeground(new Color(0, 150, 0));
        for (JButton b : new JButton[]{btnSkinRojo, btnSkinAzul, btnSkinVerde}) {
            b.setFont(new Font("Arial", Font.BOLD, 16));
        }
        filaSkins.add(labelSkin);
        filaSkins.add(btnSkinRojo);
        filaSkins.add(btnSkinAzul);
        filaSkins.add(btnSkinVerde);

        // Fila de borde P1
        filaBorde1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        filaBorde1.setBackground(COLOR_FONDO);
        JLabel labelBorde1 = new JLabel("Borde P1:  ");
        labelBorde1.setFont(new Font("Arial", Font.BOLD, 16));
        filaBorde1.add(labelBorde1);
        borde1Botones = new JButton[BORDER_NAMES.length];
        for (int i = 0; i < BORDER_NAMES.length; i++) {
            borde1Botones[i] = new JButton(BORDER_NAMES[i]);
            borde1Botones[i].setFont(new Font("Arial", Font.BOLD, 14));
            filaBorde1.add(borde1Botones[i]);
        }

        // Fila de borde P2 (visible solo en PvsP/PvsM)
        filaBorde2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        filaBorde2.setBackground(COLOR_FONDO);
        JLabel labelBorde2 = new JLabel("Borde P2:  ");
        labelBorde2.setFont(new Font("Arial", Font.BOLD, 16));
        filaBorde2.add(labelBorde2);
        borde2Botones = new JButton[BORDER_NAMES.length];
        for (int i = 0; i < BORDER_NAMES.length; i++) {
            borde2Botones[i] = new JButton(BORDER_NAMES[i]);
            borde2Botones[i].setFont(new Font("Arial", Font.BOLD, 14));
            filaBorde2.add(borde2Botones[i]);
        }
        filaBorde2.setVisible(false);

        centro.add(filaModos);
        centro.add(filaSkins);
        centro.add(filaBorde1);
        centro.add(filaBorde2);
        panelSeleccion.add(centro, BorderLayout.CENTER);

        // Botones de navegación
        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        filaBotones.setBackground(COLOR_FONDO);
        btnVolverSeleccion = new JButton("VOLVER");
        btnVolverSeleccion.setFont(new Font("Arial", Font.BOLD, 18));
        btnVolverSeleccion.setForeground(new Color(150, 0, 200));
        btnJugarSeleccion = new JButton("JUGAR");
        btnJugarSeleccion.setFont(new Font("Arial", Font.BOLD, 18));
        btnJugarSeleccion.setForeground(Color.RED);
        filaBotones.add(btnVolverSeleccion);
        filaBotones.add(btnJugarSeleccion);
        panelSeleccion.add(filaBotones, BorderLayout.SOUTH);

        panel.add(panelSeleccion, PANEL_SELECCION);
    }

    private void prepareElementsPanelJuego() {
        panelJuego = new JPanel(new BorderLayout());

        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(Color.BLACK);
        panelInfo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        menu = new JButton("MENU");
        menu.setForeground(Color.WHITE);
        menu.setBackground(Color.BLACK);
        menu.setFont(new Font("Arial", Font.BOLD, 14));
        menu.setBorderPainted(false);

        niveles = new JLabel("Nivel: 1/2");
        niveles.setForeground(Color.WHITE);
        niveles.setFont(new Font("Arial", Font.BOLD, 14));

        monedas = new JLabel("Monedas: 0/0");
        monedas.setForeground(Color.WHITE);
        monedas.setFont(new Font("Arial", Font.BOLD, 14));

        muertes = new JLabel("MUERTES: 0");
        muertes.setForeground(Color.WHITE);
        muertes.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel centerInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        centerInfo.setBackground(Color.BLACK);
        centerInfo.add(niveles);
        centerInfo.add(monedas);

        panelInfo.add(menu, BorderLayout.WEST);
        panelInfo.add(centerInfo, BorderLayout.CENTER);
        panelInfo.add(muertes, BorderLayout.EAST);

        tablero = new BoardPanel();
        tablero.setGame(juego);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(Color.BLACK);
        panelSur.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel labelSuaMunar = new JLabel("SUA-MUNAR");
        labelSuaMunar.setForeground(Color.WHITE);
        labelSuaMunar.setFont(new Font("Arial", Font.BOLD, 14));
        panelSur.add(labelSuaMunar, BorderLayout.WEST);

        tiempo = new JLabel("Tiempo: 60");
        tiempo.setForeground(Color.WHITE);
        tiempo.setFont(new Font("Arial", Font.BOLD, 14));
        tiempo.setHorizontalAlignment(SwingConstants.CENTER);
        panelSur.add(tiempo, BorderLayout.CENTER);

        panelJuego.add(panelInfo, BorderLayout.NORTH);
        panelJuego.add(tablero, BorderLayout.CENTER);
        panelJuego.add(panelSur, BorderLayout.SOUTH);

        panel.add(panelJuego, PANEL_JUEGO);
    }

    private void prepareActions() {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { exit(); }
        });

        playGame.addActionListener(e -> cardLayout.show(panel, PANEL_EXPLICACION));
        backInicio.addActionListener(e -> cardLayout.show(panel, PANEL_INICIO));

        playGame2.addActionListener(e -> cardLayout.show(panel, PANEL_SELECCION));

        btnVolverSeleccion.addActionListener(e -> cardLayout.show(panel, PANEL_EXPLICACION));

        highlightButton(btnModePlayer, btnModePvsP, btnModePvsM);
        highlightButton(btnSkinRojo, btnSkinAzul, btnSkinVerde);
        refreshBorderButtonStates();

        btnModePlayer.addActionListener(e -> {
            selectedMode = TheDOPOHardestGame.GameMode.PLAYER;
            highlightButton(btnModePlayer, btnModePvsP, btnModePvsM);
            filaBorde2.setVisible(false);
            refreshBorderButtonStates();
        });
        btnModePvsP.addActionListener(e -> {
            selectedMode = TheDOPOHardestGame.GameMode.PvsP;
            highlightButton(btnModePvsP, btnModePlayer, btnModePvsM);
            filaBorde2.setVisible(true);
            if (selectedBorder1.equals(selectedBorder2)) {
                for (Color c : BORDER_COLORS) {
                    if (!c.equals(selectedBorder1)) { selectedBorder2 = c; break; }
                }
            }
            refreshBorderButtonStates();
        });
        btnModePvsM.addActionListener(e -> {
            selectedMode = TheDOPOHardestGame.GameMode.PvsM;
            highlightButton(btnModePvsM, btnModePlayer, btnModePvsP);
            filaBorde2.setVisible(true);
            if (selectedBorder1.equals(selectedBorder2)) {
                for (Color c : BORDER_COLORS) {
                    if (!c.equals(selectedBorder1)) { selectedBorder2 = c; break; }
                }
            }
            refreshBorderButtonStates();
        });

        for (int i = 0; i < BORDER_NAMES.length; i++) {
            final int idx = i;
            borde1Botones[i].addActionListener(e -> {
                selectedBorder1 = BORDER_COLORS[idx];
                if (selectedBorder1.equals(selectedBorder2)) {
                    // P2 también tenía este color, hay que cambiarle el de P2 al primer disponible
                    for (Color c : BORDER_COLORS) {
                        if (!c.equals(selectedBorder1)) { selectedBorder2 = c; break; }
                    }
                }
                refreshBorderButtonStates();
            });
            borde2Botones[i].addActionListener(e -> {
                selectedBorder2 = BORDER_COLORS[idx];
                if (selectedBorder1.equals(selectedBorder2)) {
                    for (Color c : BORDER_COLORS) {
                        if (!c.equals(selectedBorder2)) { selectedBorder1 = c; break; }
                    }
                }
                refreshBorderButtonStates();
            });
        }

        btnSkinRojo.addActionListener(e -> {
            selectedSkin = "red";
            highlightButton(btnSkinRojo, btnSkinAzul, btnSkinVerde);
        });
        btnSkinAzul.addActionListener(e -> {
            selectedSkin = "blue";
            highlightButton(btnSkinAzul, btnSkinRojo, btnSkinVerde);
        });
        btnSkinVerde.addActionListener(e -> {
            selectedSkin = "green";
            highlightButton(btnSkinVerde, btnSkinRojo, btnSkinAzul);
        });

        btnJugarSeleccion.addActionListener(e -> {
            juego.setGameMode(selectedMode);
            juego.setPlayerType(0, selectedSkin);
            juego.setPlayerBorderColor(0, selectedBorder1);
            if (selectedMode == TheDOPOHardestGame.GameMode.PvsP
                    || selectedMode == TheDOPOHardestGame.GameMode.PvsM) {
                juego.setPlayerType(1, selectedSkin);
                juego.setPlayerBorderColor(1, selectedBorder2);
            }
            juego.startGame();
            cardLayout.show(panel, PANEL_JUEGO);
            SwingUtilities.invokeLater(() -> tablero.requestFocusInWindow());
            gameLoop.start();
        });

        menu.addActionListener(e -> {
            gameLoop.stop();
            cardLayout.show(panel, PANEL_INICIO);
        });

        salir.addActionListener(e -> exit());

        nuevaPartida.addActionListener(e -> {
            gameLoop.stop();
            int confirm = JOptionPane.showConfirmDialog(this, "¿Iniciar una nueva partida?",
                "Nueva Partida", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                keysDownPlayer1.clear();
                keysDownPlayer2.clear();
                juego.startGame();
                cardLayout.show(panel, PANEL_JUEGO);
                SwingUtilities.invokeLater(() -> tablero.requestFocusInWindow());
                gameLoop.start();
            } else if (panelJuego.isShowing()) {
                gameLoop.start();
            }
        });

        pausar.addActionListener(e -> {
            juego.togglePause();
            pausar.setText(juego.isPaused() ? "Reanudar" : "Pausar");
        });

        reiniciar.addActionListener(e -> {
            gameLoop.stop();
            keysDownPlayer1.clear();
                keysDownPlayer2.clear();
            juego.restartLevel();
            cardLayout.show(panel, PANEL_JUEGO);
            SwingUtilities.invokeLater(() -> tablero.requestFocusInWindow());
            gameLoop.start();
        });
        guardarPartida.addActionListener(e -> {
            gameLoop.stop();
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    juego.guardarPartida(fc.getSelectedFile());
                    JOptionPane.showMessageDialog(this, "Partida guardada exitosamente.");
                } catch (domain.GameException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (panelJuego.isShowing()) gameLoop.start();
        });

        abrirPartida.addActionListener(e -> {
            gameLoop.stop();
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    juego.abrirPartida(fc.getSelectedFile());
                    cardLayout.show(panel, PANEL_JUEGO);
                    SwingUtilities.invokeLater(() -> tablero.requestFocusInWindow());
                    gameLoop.start();
                    JOptionPane.showMessageDialog(this, "Partida cargada exitosamente.");
                    return;
                } catch (domain.GameException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (panelJuego.isShowing()) gameLoop.start();
        });

        exportarNivel.addActionListener(e -> {
            gameLoop.stop();
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    juego.exportarNivel(fc.getSelectedFile());
                    JOptionPane.showMessageDialog(this, "Nivel exportado exitosamente.");
                } catch (domain.GameException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (panelJuego.isShowing()) gameLoop.start();
        });

        importarNivel.addActionListener(e -> {
            gameLoop.stop();
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    juego.importarNivel(fc.getSelectedFile());
                    cardLayout.show(panel, PANEL_JUEGO);
                    SwingUtilities.invokeLater(() -> tablero.requestFocusInWindow());
                    gameLoop.start();
                    JOptionPane.showMessageDialog(this, "Nivel importado exitosamente.");
                    return;
                } catch (domain.GameException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (panelJuego.isShowing()) gameLoop.start();
        });

        tablero.setFocusable(true);

        tablero.addKeyListener(new KeyAdapter() {
            private static final Set<Integer> PVP_KEYS = new HashSet<>(java.util.Arrays.asList(
                KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT
            ));

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    juego.togglePause();
                    pausar.setText(juego.isPaused() ? "Reanudar" : "Pausar");
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

    public void update() {
    	updatePlayer(0, keysDownPlayer1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D);
    	if (juego.getGameMode() == TheDOPOHardestGame.GameMode.PvsP)
    	    updatePlayer(1, keysDownPlayer2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
        juego.update();
    }
    
    private void updatePlayer(int index, Set<Integer> keys, int up, int down, int left, int right) {
        double dx = 0, dy = 0;
        if (keys.contains(up))    dy -= 1;
        if (keys.contains(down))  dy += 1;
        if (keys.contains(left))  dx -= 1;
        if (keys.contains(right)) dx += 1;
        if (dx != 0 || dy != 0) juego.movePlayer(index, dx, dy);
    }

    public void refresh() {
        if (juego.getCurrentLevel() != null) {
            boolean pvsp = juego.getGameMode() == TheDOPOHardestGame.GameMode.PvsP;
            if (pvsp) {
                muertes.setText("P1: " + juego.getPlayerDeaths(0) + " muertes  |  P2: " + juego.getPlayerDeaths(1) + " muertes");
            } else {
                muertes.setText("MUERTES: " + juego.getPlayerDeaths(0));
            }
            if (pvsp) {
                monedas.setText("P1: " + juego.getPlayerCoins(0) + "/" + juego.getPlayerTotalCoins(0)
                    + "  |  P2: " + juego.getPlayerCoins(1) + "/" + juego.getPlayerTotalCoins(1));
            } else {
                monedas.setText("Monedas: " + juego.getPlayerCoins(0) + "/" + juego.getPlayerTotalCoins(0));
            }
            niveles.setText("Nivel: " + juego.getLevelNumber());
            tiempo.setText(pvsp ? "" : "Tiempo: " + String.format("%.0f", juego.getRemainingTime()));

            if (juego.isLevelComplete()) {
                boolean hasNext = juego.hasNextLevel();
                juego.advanceLevel();
                if (!hasNext) {
                    gameLoop.stop();
                    keysDownPlayer1.clear();
                    keysDownPlayer2.clear();
                    if (pvsp) {
                        mostrarResultadoPvsP();
                    } else {
                        JOptionPane.showMessageDialog(this, "¡HAS GANADO EL JUEGO!");
                    }
                    cardLayout.show(panel, PANEL_INICIO);
                }
            } else if (juego.isGameOver()) {
                gameLoop.stop();
                keysDownPlayer1.clear();
                keysDownPlayer2.clear();
                JOptionPane.showMessageDialog(this, "¡Tiempo agotado!");
                cardLayout.show(panel, PANEL_INICIO);
            }
        }
        tablero.refresh();
    }

    private void mostrarResultadoPvsP() {
        java.util.Map<String, Integer> levelsWon = juego.getLevelsWon();
        int wonP1 = levelsWon.getOrDefault("Player1", 0);
        int wonP2 = levelsWon.getOrDefault("Player2", 0);
        String ganador = wonP1 > wonP2 ? "Player1" : wonP2 > wonP1 ? "Player2" : "Empate";
        String msg = "=== RESULTADO FINAL ===\n"
            + "Player1 — Niveles ganados: " + wonP1
            + "  |  Muertes: " + juego.getPlayerDeaths(0)
            + "  |  Monedas: " + juego.getPlayerCoins(0) + "\n"
            + "Player2 — Niveles ganados: " + wonP2
            + "  |  Muertes: " + juego.getPlayerDeaths(1)
            + "  |  Monedas: " + juego.getPlayerCoins(1) + "\n\n"
            + (ganador.equals("Empate") ? "¡EMPATE!" : "¡Ganó " + ganador + "!");
        JOptionPane.showMessageDialog(this, msg, "Fin del juego", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshBorderButtonStates() {
        boolean twoPlayers = selectedMode != TheDOPOHardestGame.GameMode.PLAYER;
        for (int i = 0; i < BORDER_COLORS.length; i++) {
            Color c = BORDER_COLORS[i];
            applyBorderButtonStyle(borde1Botones[i], c,
                c.equals(selectedBorder1),
                twoPlayers && c.equals(selectedBorder2));
            applyBorderButtonStyle(borde2Botones[i], c,
                c.equals(selectedBorder2),
                twoPlayers && c.equals(selectedBorder1));
        }
        if (panelSeleccion != null) {
            panelSeleccion.revalidate();
            panelSeleccion.repaint();
        }
    }

    private void applyBorderButtonStyle(JButton b, Color borderColor, boolean selectedHere, boolean takenByOther) {
        b.setBackground(borderColor);
        b.setForeground(borderColor.equals(Color.BLACK) ? Color.WHITE : Color.BLACK);
        b.setOpaque(true);
        b.setBorderPainted(true);
        if (selectedHere) {
            b.setBorder(BorderFactory.createLineBorder(new Color(30, 80, 180), 4));
        } else {
            b.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        }
        b.setEnabled(!takenByOther);
    }

    private void highlightButton(JButton selected, JButton... others) {
        selected.setBackground(new Color(30, 80, 180));
        selected.setForeground(Color.WHITE);
        selected.setOpaque(true);
        selected.setBorderPainted(false);
        for (JButton b : others) {
            b.setBackground(UIManager.getColor("Button.background"));
            b.setForeground(Color.BLACK);
            b.setOpaque(false);
            b.setBorderPainted(true);
        }
    }

    private void exit() {
        int option = JOptionPane.showConfirmDialog(this, "¿Desea cerrar la aplicación?",
            "Confirmar salida", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) System.exit(0);
    }
}
