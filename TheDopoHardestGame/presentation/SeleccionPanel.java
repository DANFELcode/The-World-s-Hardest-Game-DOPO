package presentation;

import domain.GameMode;
import domain.TheDOPOHardestGame;
import java.awt.*;
import javax.swing.*;

/**
 * Selection view: lets the user pick the game mode, the player skin, the
 * border color(s) and the level, with a live preview of the character.
 * On "JUGAR" it configures the facade and asks the host to start the game.
 */
public class SeleccionPanel extends GradientPanel {

    private static final Color[] BORDER_COLORS =
        {Color.BLACK, Color.WHITE, Color.YELLOW, Color.CYAN, Color.MAGENTA};
    private static final Color ACCENT     = new Color(40, 110, 230);
    private static final Color SKIN_RED   = new Color(220, 55, 55);
    private static final Color SKIN_BLUE  = new Color(55, 90, 220);
    private static final Color SKIN_GREEN = new Color(0, 150, 0);

    private final TheDOPOHardestGame juego;
    private final MainView host;

    private JButton btnModePlayer, btnModePvsP, btnModePvsM;
    private JButton btnSkinRojo, btnSkinAzul, btnSkinVerde;
    private JButton btnStrategyRandom, btnStrategyExpert;
    private JButton btnJugar, btnVolver;
    private JButton[] borde1Botones;
    private JButton[] borde2Botones;
    private JPanel borde2Controls;
    private JPanel levelControls;
    private JPanel strategyControls;
    private JLabel labelBorde2;
    private JLabel labelStrategy;
    private PlayerPreviewPanel playerPreview;

    private int selectedLevel = 1;
    private GameMode selectedMode = GameMode.PLAYER;
    private String selectedSkin = "red";
    private Color selectedBorder1 = Color.BLACK;
    private Color selectedBorder2 = Color.WHITE;
    private String selectedStrategy = "random";

    /**
     * @param juego the game facade
     * @param host the main window, used for navigation and loop control
     */
    public SeleccionPanel(TheDOPOHardestGame juego, MainView host) {
        super(new BorderLayout());
        this.juego = juego;
        this.host = host;
        construirUI();
        wireAcciones();
        highlightButton(btnModePlayer, btnModePvsP, btnModePvsM);
        highlightButton(btnSkinRojo, btnSkinAzul, btnSkinVerde);
        highlightButton(btnStrategyRandom, btnStrategyExpert);
        refreshBorderButtonStates();
        refreshLevelButtons();
        updatePreview();
    }

    private void construirUI() {
        JComponent titulo = UIFactory.createStyledTitle(
            "Seleccione el modo de juego", new Color(245, 246, 255));
        titulo.setBorder(BorderFactory.createEmptyBorder(14, 0, 6, 0));
        add(titulo, BorderLayout.NORTH);

        // --- Controles, en una rejilla con etiquetas alineadas ---
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 14));
        // Reserve room for all 5 rows (incl. the optional Borde P2 row) so the
        // window packs large enough and toggling that row never compresses the layout.
        centro.setPreferredSize(new Dimension(680, 440));

        btnModePlayer = UIFactory.createPillButton("PLAYER", ACCENT);
        btnModePvsP   = UIFactory.createPillButton("PvsP", ACCENT);
        btnModePvsM   = UIFactory.createPillButton("PvsM", ACCENT);

        btnSkinRojo  = UIFactory.createPillButton("Blinky (Rojo)", SKIN_RED);
        btnSkinAzul  = UIFactory.createPillButton("Inky (Azul)", SKIN_BLUE);
        btnSkinVerde = UIFactory.createPillButton("Clyde (Verde)", SKIN_GREEN);

        btnStrategyRandom = UIFactory.createPillButton("Aleatoria", ACCENT);
        btnStrategyExpert = UIFactory.createPillButton("Experta", ACCENT);

        borde1Botones = new JButton[BORDER_COLORS.length];
        borde2Botones = new JButton[BORDER_COLORS.length];
        JPanel borde1Controls = fila();
        borde2Controls = fila();
        for (int i = 0; i < BORDER_COLORS.length; i++) {
            borde1Botones[i] = UIFactory.createColorButton(BORDER_COLORS[i]);
            borde2Botones[i] = UIFactory.createColorButton(BORDER_COLORS[i]);
            borde1Controls.add(borde1Botones[i]);
            borde2Controls.add(borde2Botones[i]);
        }

        levelControls = fila();
        strategyControls = fila(btnStrategyRandom, btnStrategyExpert);
        labelBorde2 = rowLabel("Borde P2:");
        labelStrategy = rowLabel("IA Maquina:");

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 10);
        addRow(centro, gc, 0, rowLabel("Modo:"),
            fila(btnModePlayer, btnModePvsP, btnModePvsM));
        addRow(centro, gc, 1, rowLabel("Skin:"),
            fila(btnSkinRojo, btnSkinAzul, btnSkinVerde));
        addRow(centro, gc, 2, rowLabel("Borde P1:"), borde1Controls);
        addRow(centro, gc, 3, labelBorde2, borde2Controls);
        addRow(centro, gc, 4, labelStrategy, strategyControls);
        addRow(centro, gc, 5, rowLabel("Nivel:"), levelControls);

        labelBorde2.setVisible(false);
        borde2Controls.setVisible(false);
        labelStrategy.setVisible(false);
        strategyControls.setVisible(false);

        // --- Preview del personaje sobre el fondo del tablero ---
        playerPreview = new PlayerPreviewPanel();
        JPanel previewWrapper = new JPanel(new BorderLayout());
        previewWrapper.setOpaque(false);
        previewWrapper.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 12));
        previewWrapper.add(playerPreview, BorderLayout.CENTER);

        JPanel split = new JPanel(new BorderLayout());
        split.setOpaque(false);
        split.add(centro, BorderLayout.CENTER);
        split.add(previewWrapper, BorderLayout.EAST);
        add(split, BorderLayout.CENTER);

        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 16));
        filaBotones.setOpaque(false);
        btnVolver = UIFactory.createStyledButton("VOLVER", new Color(0x9A, 0x4B, 0xC1));
        btnJugar  = UIFactory.createStyledButton("JUGAR", new Color(0xEB, 0x55, 0x55));
        filaBotones.add(btnVolver);
        filaBotones.add(btnJugar);
        add(filaBotones, BorderLayout.SOUTH);
    }

    /** Builds a left-aligned flow row holding the given controls. */
    private JPanel fila(JComponent... comps) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        p.setOpaque(false);
        for (JComponent c : comps) p.add(c);
        return p;
    }

    private JLabel rowLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 17));
        l.setForeground(new Color(45, 50, 70));
        return l;
    }

    /** Adds a [label | controls] row to the GridBag grid. */
    private void addRow(JPanel grid, GridBagConstraints gc, int row, JComponent label, JComponent controls) {
        gc.gridy = row;
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.weightx = 0;
        grid.add(label, gc);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.weightx = 1;
        grid.add(controls, gc);
    }

    private void wireAcciones() {
        btnVolver.addActionListener(e -> host.mostrarExplicacion());

        btnModePlayer.addActionListener(e -> {
            selectedMode = GameMode.PLAYER;
            selectedLevel = 1;
            highlightButton(btnModePlayer, btnModePvsP, btnModePvsM);
            setBorde2Visible(false);
            setStrategyVisible(false);
            refreshBorderButtonStates();
            refreshLevelButtons();
            updatePreview();
        });
        btnModePvsP.addActionListener(e -> {
            selectedMode = GameMode.PvsP;
            selectedLevel = 1;
            highlightButton(btnModePvsP, btnModePlayer, btnModePvsM);
            setBorde2Visible(true);
            setStrategyVisible(false);
            ensureBordersDiffer();
            refreshBorderButtonStates();
            refreshLevelButtons();
            updatePreview();
        });
        btnModePvsM.addActionListener(e -> {
            selectedMode = GameMode.PvsM;
            selectedLevel = 1;
            highlightButton(btnModePvsM, btnModePlayer, btnModePvsP);
            setBorde2Visible(true);
            setStrategyVisible(true);
            ensureBordersDiffer();
            refreshBorderButtonStates();
            refreshLevelButtons();
            updatePreview();
        });

        btnStrategyRandom.addActionListener(e -> {
            selectedStrategy = "random";
            highlightButton(btnStrategyRandom, btnStrategyExpert);
        });
        btnStrategyExpert.addActionListener(e -> {
            selectedStrategy = "expert";
            highlightButton(btnStrategyExpert, btnStrategyRandom);
        });

        for (int i = 0; i < BORDER_COLORS.length; i++) {
            final int idx = i;
            borde1Botones[i].addActionListener(e -> {
                selectedBorder1 = BORDER_COLORS[idx];
                if (selectedBorder1.equals(selectedBorder2)) {
                    for (Color c : BORDER_COLORS) {
                        if (!c.equals(selectedBorder1)) { selectedBorder2 = c; break; }
                    }
                }
                refreshBorderButtonStates();
                updatePreview();
            });
            borde2Botones[i].addActionListener(e -> {
                selectedBorder2 = BORDER_COLORS[idx];
                if (selectedBorder1.equals(selectedBorder2)) {
                    for (Color c : BORDER_COLORS) {
                        if (!c.equals(selectedBorder2)) { selectedBorder1 = c; break; }
                    }
                }
                refreshBorderButtonStates();
                updatePreview();
            });
        }

        btnSkinRojo.addActionListener(e -> {
            selectedSkin = "red";
            highlightButton(btnSkinRojo, btnSkinAzul, btnSkinVerde);
            updatePreview();
        });
        btnSkinAzul.addActionListener(e -> {
            selectedSkin = "blue";
            highlightButton(btnSkinAzul, btnSkinRojo, btnSkinVerde);
            updatePreview();
        });
        btnSkinVerde.addActionListener(e -> {
            selectedSkin = "green";
            highlightButton(btnSkinVerde, btnSkinRojo, btnSkinAzul);
            updatePreview();
        });

        btnJugar.addActionListener(e -> {
            juego.setGameMode(selectedMode);
            juego.setPlayerType("Player1", selectedSkin);
            juego.setPlayerBorderColor("Player1", selectedBorder1);
            if (selectedMode == GameMode.PvsP || selectedMode == GameMode.PvsM) {
                juego.setPlayerType("Player2", selectedSkin);
                juego.setPlayerBorderColor("Player2", selectedBorder2);
            }
            if (selectedMode == GameMode.PvsM) {
                juego.setMachineStrategy(selectedStrategy);
            }
            juego.startGame(selectedLevel);
            host.iniciarJuego();
        });
    }

    private void setBorde2Visible(boolean visible) {
        labelBorde2.setVisible(visible);
        borde2Controls.setVisible(visible);
    }

    private void setStrategyVisible(boolean visible) {
        labelStrategy.setVisible(visible);
        strategyControls.setVisible(visible);
    }

    private void updatePreview() {
        boolean two = selectedMode == GameMode.PvsP || selectedMode == GameMode.PvsM;
        boolean isMachine = selectedMode == GameMode.PvsM;
        playerPreview.setSkin1(selectedSkin);
        playerPreview.setBorder1(selectedBorder1);
        playerPreview.setSkin2(selectedSkin);
        playerPreview.setBorder2(selectedBorder2);
        playerPreview.setShowTwo(two);
        playerPreview.setLabel2(isMachine ? "MAQUINA" : "P2");
        labelBorde2.setText(isMachine ? "Borde Maquina:" : "Borde P2:");
    }

    private void refreshLevelButtons() {
        levelControls.removeAll();
        juego.setGameMode(selectedMode);
        int count = juego.getAvailableLevelCount();
        if (selectedLevel > count) selectedLevel = 1;
        for (int i = 1; i <= count; i++) {
            final int lvl = i;
            JButton btn = UIFactory.createPillButton(String.valueOf(i), ACCENT);
            if (lvl == selectedLevel) btn.putClientProperty("flat-selected", Boolean.TRUE);
            btn.addActionListener(e -> {
                selectedLevel = lvl;
                refreshLevelButtons();
            });
            levelControls.add(btn);
        }
        levelControls.revalidate();
        levelControls.repaint();
    }

    private void ensureBordersDiffer() {
        if (selectedBorder1.equals(selectedBorder2)) {
            for (Color c : BORDER_COLORS) {
                if (!c.equals(selectedBorder1)) { selectedBorder2 = c; break; }
            }
        }
    }

    private void refreshBorderButtonStates() {
        boolean twoPlayers = selectedMode != GameMode.PLAYER;
        for (int i = 0; i < BORDER_COLORS.length; i++) {
            Color c = BORDER_COLORS[i];
            applyBorderButtonStyle(borde1Botones[i],
                c.equals(selectedBorder1), twoPlayers && c.equals(selectedBorder2));
            applyBorderButtonStyle(borde2Botones[i],
                c.equals(selectedBorder2), twoPlayers && c.equals(selectedBorder1));
        }
        revalidate();
        repaint();
    }

    private void applyBorderButtonStyle(JButton b, boolean selectedHere, boolean takenByOther) {
        b.putClientProperty("flat-selected", selectedHere ? Boolean.TRUE : Boolean.FALSE);
        b.setEnabled(!takenByOther);
        b.repaint();
    }

    private void highlightButton(JButton selected, JButton... others) {
        selected.putClientProperty("flat-selected", Boolean.TRUE);
        selected.repaint();
        for (JButton b : others) {
            b.putClientProperty("flat-selected", Boolean.FALSE);
            b.repaint();
        }
    }
}
