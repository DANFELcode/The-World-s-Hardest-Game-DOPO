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
    private JMenuItem guardarPartida, abrirPartida;

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

    private JPanel panelJuego;
    private BoardPanel tablero;
    private JButton menu;
    private JLabel niveles;
    private JLabel muertes;
    private JLabel monedas;
    private JLabel tiempo;

    private GameLoop gameLoop;

    private final Set<Integer> keysDown = new HashSet<>();

    private static final Color COLOR_FONDO        = new Color(180, 180, 220);
    private static final String PANEL_INICIO      = "inicio";
    private static final String PANEL_EXPLICACION = "explicacion";
    private static final String PANEL_JUEGO       = "juego";

    public TheDOPOHardestGameGUI() {
        super("TheDOPOHardestGame");
        juego = TheDOPOHardestGame.getInstance();

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
        prepareElementsPanelJuego();
        prepareElementsMenuBar();

        this.add(panel);
    }

    private void prepareElementsMenuBar() {
        menuBar = new JMenuBar();

        archivo = new JMenu("Archivo");
        guardarPartida = new JMenuItem("Guardar Partida");
        abrirPartida = new JMenuItem("Abrir Partida");
        archivo.add(guardarPartida);
        archivo.add(abrirPartida);

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

        playGame2.addActionListener(e -> {
            juego.loadTestLevel();
            tablero.setLevel(juego.getCurrentLevel());
            cardLayout.show(panel, PANEL_JUEGO);
            SwingUtilities.invokeLater(() -> tablero.requestFocusInWindow());
            gameLoop.start();
        });

        menu.addActionListener(e -> {
            gameLoop.stop();
            cardLayout.show(panel, PANEL_INICIO);
        });

        String msg = "En implementación, próximamente.";
        salir.addActionListener(e -> exit());
        nuevaPartida.addActionListener(e -> JOptionPane.showMessageDialog(this, msg));
        pausar.addActionListener(e -> JOptionPane.showMessageDialog(this, msg));
        reiniciar.addActionListener(e -> JOptionPane.showMessageDialog(this, msg));
        guardarPartida.addActionListener(e -> JOptionPane.showMessageDialog(this, msg));
        abrirPartida.addActionListener(e -> JOptionPane.showMessageDialog(this, msg));

        tablero.setFocusable(true);

        tablero.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    juego.togglePause();
                    return;
                }
                keysDown.add(e.getKeyCode());
            }
            @Override
            public void keyReleased(KeyEvent e) {
                keysDown.remove(e.getKeyCode());
            }
        });
    }

    public void update() {
        double dx = 0, dy = 0;
        if (keysDown.contains(KeyEvent.VK_UP)    || keysDown.contains(KeyEvent.VK_W)) dy -= 1;
        if (keysDown.contains(KeyEvent.VK_DOWN)  || keysDown.contains(KeyEvent.VK_S)) dy += 1;
        if (keysDown.contains(KeyEvent.VK_LEFT)  || keysDown.contains(KeyEvent.VK_A)) dx -= 1;
        if (keysDown.contains(KeyEvent.VK_RIGHT) || keysDown.contains(KeyEvent.VK_D)) dx += 1;
        if (dx != 0 || dy != 0) juego.movePlayer(0, dx, dy);
        juego.update();
    }

    public void refresh() {
        if (juego.getCurrentLevel() != null) {
            muertes.setText("MUERTES: " + juego.getPlayerDeaths(0));
            monedas.setText("Monedas: " + juego.getCollectedCoins() + "/" + juego.getTotalCoins());
            niveles.setText("Nivel: " + juego.getLevelNumber() + "/2");
            tiempo.setText("Tiempo: " + String.format("%.0f", juego.getRemainingTime()));

            if (juego.isLevelComplete()) {
                if (juego.getLevelNumber() == 1) {
                    juego.advanceLevel();
                    tablero.setLevel(juego.getCurrentLevel());
                } else {
                    gameLoop.stop();
                    keysDown.clear();
                    JOptionPane.showMessageDialog(this, "¡HAS GANADO EL JUEGO!");
                    cardLayout.show(panel, PANEL_INICIO);
                }
            } else if (juego.isGameOver()) {
                gameLoop.stop();
                keysDown.clear();
                JOptionPane.showMessageDialog(this, "¡Tiempo agotado!");
                cardLayout.show(panel, PANEL_INICIO);
            }
        }
        tablero.refresh();
    }

    private void exit() {
        int option = JOptionPane.showConfirmDialog(this, "¿Desea cerrar la aplicación?",
            "Confirmar salida", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) System.exit(0);
    }
}
