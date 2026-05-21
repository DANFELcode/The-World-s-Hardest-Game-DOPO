package presentation;

import domain.TheDOPOHardestGame;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class TheDOPOHardestGameGUI extends JFrame implements MainView {

    private TheDOPOHardestGame juego;
    private JMenuBar menuBar;
    private JMenu opciones, archivo;
    private JMenuItem nuevaPartida, pausar, salir, reiniciar;
    private JMenuItem guardarPartida, abrirPartida, exportarNivel, importarNivel;

    private JPanel panel;
    private CardLayout cardLayout;

    private InicioPanel panelInicio;
    private ExpPanel panelExp;
    private SeleccionPanel panelSeleccion;

    private JuegoPanel panelJuego;

    private GameLoop gameLoop;

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
        
        prepareElementsMenuBar();
        prepareElementsPanelInicio();
        prepareElementsPanelExp();
        prepareElementsPanelSeleccion();
        prepareElementsPanelJuego();

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
        panelInicio = new InicioPanel(this);
        panel.add(panelInicio, PANEL_INICIO);
    }

    private void prepareElementsPanelExp() {
        panelExp = new ExpPanel(this);
        panel.add(panelExp, PANEL_EXPLICACION);
    }

    private void prepareElementsPanelSeleccion() {
        panelSeleccion = new SeleccionPanel(juego, this);
        panel.add(panelSeleccion, PANEL_SELECCION);
    }

    private void prepareElementsPanelJuego() {
        panelJuego = new JuegoPanel(juego, this);
        panel.add(panelJuego, PANEL_JUEGO);
    }

    private void prepareActions() {
        prepareActionsWindow();
        prepareActionsMenuArchivo();
        prepareActionsMenuOpciones();
    }

    private void prepareActionsWindow() {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { exit(); }
        });
    }

    private void prepareActionsMenuArchivo() {
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
                    SwingUtilities.invokeLater(() -> panelJuego.requestBoardFocus());
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
            JFileChooser fc = new JFileChooser(new java.io.File("resources/levels/user"));
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
            JFileChooser fc = new JFileChooser(new java.io.File("resources/levels/user"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    juego.importarNivel(fc.getSelectedFile());
                    cardLayout.show(panel, PANEL_JUEGO);
                    SwingUtilities.invokeLater(() -> panelJuego.requestBoardFocus());
                    gameLoop.start();
                    JOptionPane.showMessageDialog(this, "Nivel importado exitosamente.");
                    return;
                } catch (domain.GameException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (panelJuego.isShowing()) gameLoop.start();
        });
    }

    private void prepareActionsMenuOpciones() {
        salir.addActionListener(e -> exit());

        nuevaPartida.addActionListener(e -> {
            gameLoop.stop();
            int confirm = JOptionPane.showConfirmDialog(this, "¿Iniciar una nueva partida?",
                "Nueva Partida", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                panelJuego.clearKeys();
                juego.startGame(1);
                cardLayout.show(panel, PANEL_JUEGO);
                SwingUtilities.invokeLater(() -> panelJuego.requestBoardFocus());
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
            panelJuego.clearKeys();
            juego.restartLevel();
            cardLayout.show(panel, PANEL_JUEGO);
            SwingUtilities.invokeLater(() -> panelJuego.requestBoardFocus());
            gameLoop.start();
        });
    }

    /** Called by GameLoop each tick — delegates input/update to the game panel. */
    public void update() { panelJuego.update(); }

    /** Called by GameLoop each frame — delegates HUD/render to the game panel. */
    public void refresh() { panelJuego.refresh(); }

    @Override
    public void detenerLoop() { gameLoop.stop(); }

    @Override
    public void mostrarInicio() { cardLayout.show(panel, PANEL_INICIO); }

    @Override
    public void mostrarExplicacion() { cardLayout.show(panel, PANEL_EXPLICACION); }

    @Override
    public void mostrarSeleccion() { cardLayout.show(panel, PANEL_SELECCION); }

    @Override
    public void iniciarJuego() {
        cardLayout.show(panel, PANEL_JUEGO);
        SwingUtilities.invokeLater(() -> panelJuego.requestBoardFocus());
        gameLoop.start();
    }

    @Override
    public void onPausaCambiada() {
        pausar.setText(juego.isPaused() ? "Reanudar" : "Pausar");
    }

    private void exit() {
        int option = JOptionPane.showConfirmDialog(this, "¿Desea cerrar la aplicación?",
            "Confirmar salida", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) System.exit(0);
    }
}
