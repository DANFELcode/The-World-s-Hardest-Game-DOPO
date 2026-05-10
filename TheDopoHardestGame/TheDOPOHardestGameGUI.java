package presentation;

import domain.Level;
import domain.TheDOPOHardestGame;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.awt.event.*;

public class TheDOPOHardestGameGUI extends JFrame {

    private TheDOPOHardestGame juego;
    private JMenuBar menuBar;
    private JMenu opciones, archivo;
    private JMenuItem nuevaPartida, pausar, salir, reiniciar;
    private JMenuItem guardarPartida, abrirPartida;

    private JPanel panelPrincipal;
    private CardLayout cardLayout;

    private JPanel panelInicio;
    private JLabel labelTitulo;
    private JButton btnJugarInicio;
    private JButton ajustes;

    private JPanel panelExp;
    private JLabel descripcion;
    private JButton btnJugarExp;
    private JButton btnVolverInicio;

    private JPanel panelJuego;
    private BoardPanel tablero;
    private JButton menu;
    private JLabel niveles;
    private JLabel muertes;
    private JLabel monedas;
    private JLabel tiempo;

    private GameLoop gameLoop;

    private final Set<Integer> keysDown = new HashSet<>();

    private static final Color COLOR_FONDO       = new Color(180, 180, 220);
    private static final String PANEL_INICIO      = "inicio";
    private static final String PANEL_EXPLICACION = "explicacion";
    private static final String PANEL_JUEGO       = "juego";

    public TheDOPOHardestGameGUI() {
        super("TheDOPOHardestGame");
        juego = new TheDOPOHardestGame();

        prepareElements();
        prepareActions();
        // gameLoop va después de prepareElements: necesita que tablero y juego existan
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
        panelPrincipal = new JPanel(cardLayout);

        prepareElementsPanelInicio();
        prepareElementsPanelExp();
        prepareElementsPanelJuego();
        prepareElementsMenuBar();

        this.add(panelPrincipal);
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

        btnJugarInicio = new JButton("PLAY GAME");
        btnJugarInicio.setFont(new Font("Arial", Font.BOLD, 20));
        btnJugarInicio.setForeground(Color.RED);

        ajustes = new JButton("SETTINGS");
        ajustes.setFont(new Font("Arial", Font.BOLD, 20));
        ajustes.setForeground(new Color(0, 150, 0));

        panelBotones.add(btnJugarInicio);
        panelBotones.add(ajustes);

        panelInicio.add(panelTitulo, BorderLayout.CENTER);
        panelInicio.add(panelBotones, BorderLayout.SOUTH);

        panelPrincipal.add(panelInicio, PANEL_INICIO);
    }

    private void prepareElementsPanelExp() {
        panelExp = new JPanel(new BorderLayout());
        panelExp.setBackground(COLOR_FONDO);

        descripcion = new JLabel("<html><div style='text-align: justify; width: 400px'>"
            + "You are the <font color='red'><b>red</b></font> square. "
            + "Avoid the <font color='blue'><b>blue</b></font> circles and collect the "
            + "<font color='#DAA520'><b>yellow</b></font> circles. "
            + "Once you have collected all of the yellow circles, move to the "
            + "<font color='green'><b>green</b></font> beacon to complete the level. "
            + "Some levels consist of more than one beacon; the intermediary beacons act as check points. "
            + "You must complete all levels in order to submit your score. "
            + "Your score is a reflection of how many times you have died; the less, the better."
            + "</div></html>");
        descripcion.setFont(new Font("Arial", Font.PLAIN, 16));
        descripcion.setHorizontalAlignment(SwingConstants.CENTER);
        descripcion.setBorder(BorderFactory.createEmptyBorder(40, 60, 20, 60));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        panelBotones.setBackground(COLOR_FONDO);

        btnVolverInicio = new JButton("BACK TO MENU");
        btnVolverInicio.setFont(new Font("Arial", Font.BOLD, 20));
        btnVolverInicio.setForeground(new Color(150, 0, 200));

        btnJugarExp = new JButton("PLAY GAME");
        btnJugarExp.setFont(new Font("Arial", Font.BOLD, 20));
        btnJugarExp.setForeground(Color.RED);

        panelBotones.add(btnVolverInicio);
        panelBotones.add(btnJugarExp);

        panelExp.add(descripcion, BorderLayout.CENTER);
        panelExp.add(panelBotones, BorderLayout.SOUTH);

        panelPrincipal.add(panelExp, PANEL_EXPLICACION);
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

        niveles = new JLabel("Niveles: 1/1");
        niveles.setForeground(Color.WHITE);
        niveles.setFont(new Font("Arial", Font.BOLD, 14));

        monedas = new JLabel("Monedas: 0/0");
        monedas.setForeground(Color.WHITE);
        monedas.setFont(new Font("Arial", Font.BOLD, 14));

        muertes = new JLabel("Muertes: 0");
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

        JLabel labelSuaMunar = new JLabel("SUA - MUNAR");
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

        panelPrincipal.add(panelJuego, PANEL_JUEGO);
    }

    private void prepareActions() {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { exit(); }
        });

        btnJugarInicio.addActionListener(e -> cardLayout.show(panelPrincipal, PANEL_EXPLICACION));
        btnVolverInicio.addActionListener(e -> cardLayout.show(panelPrincipal, PANEL_INICIO));

        btnJugarExp.addActionListener(e -> {
            juego.loadTestLevel();
            tablero.setLevel(juego.getCurrentLevel());
            cardLayout.show(panelPrincipal, PANEL_JUEGO);
            // invokeLater: espera a que el EDT termine la transición antes de pedir el foco
            SwingUtilities.invokeLater(() -> tablero.requestFocusInWindow());
            gameLoop.start();
        });

        menu.addActionListener(e -> {
            gameLoop.stop();
            cardLayout.show(panelPrincipal, PANEL_INICIO);
        });

        String msg = "En implementación, próximamente.";
        salir.addActionListener(e -> exit());
        nuevaPartida.addActionListener(e -> JOptionPane.showMessageDialog(this, msg));
        pausar.addActionListener(e -> JOptionPane.showMessageDialog(this, msg));
        reiniciar.addActionListener(e -> JOptionPane.showMessageDialog(this, msg));
        guardarPartida.addActionListener(e -> JOptionPane.showMessageDialog(this, msg));
        abrirPartida.addActionListener(e -> JOptionPane.showMessageDialog(this, msg));

        // JPanel no recibe foco del teclado por defecto
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
        Level level = juego.getCurrentLevel();
        if (level != null) {
            if (!level.getPlayers().isEmpty()) {
                muertes.setText("DEATHS: " + level.getPlayers().get(0).getDeaths());
            }
            long collected = level.getCoins().stream().filter(c -> c.isCollected()).count();
            monedas.setText("Coins: " + collected + "/" + level.getCoins().size());
            niveles.setText("Levels: " + level.getNumber() + "/1");
            tiempo.setText("Tiempo: " + String.format("%.0f", level.getGameTime()));

            if (juego.isLevelComplete()) {
                gameLoop.stop();
                keysDown.clear();
                JOptionPane.showMessageDialog(this, "Nivel completado");
                cardLayout.show(panelPrincipal, PANEL_INICIO);
            } else if (juego.isGameOver()) {
                gameLoop.stop();
                keysDown.clear();
                JOptionPane.showMessageDialog(this, "Tiempo terminado, Perdiste.");
                cardLayout.show(panelPrincipal, PANEL_INICIO);
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
