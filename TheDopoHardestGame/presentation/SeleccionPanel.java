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

    private static final String[] BORDER_NAMES = {"NEGRO", "BLANCO", "AMARILLO", "CYAN", "MAGENTA"};
    private static final Color[] BORDER_COLORS = {Color.BLACK, Color.WHITE, Color.YELLOW, Color.CYAN, Color.MAGENTA};

    private final TheDOPOHardestGame juego;
    private final MainView host;

    private JButton btnModePlayer, btnModePvsP, btnModePvsM;
    private JButton btnSkinRojo, btnSkinAzul, btnSkinVerde;
    private JButton btnJugar, btnVolver;
    private JButton[] borde1Botones;
    private JButton[] borde2Botones;
    private JPanel filaBorde2;
    private JPanel filaLevel;
    private JLabel labelBorde2;
    private PlayerPreviewPanel playerPreview;

    private int selectedLevel = 1;
    private GameMode selectedMode = GameMode.PLAYER;
    private String selectedSkin = "red";
    private Color selectedBorder1 = Color.BLACK;
    private Color selectedBorder2 = Color.WHITE;

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
        refreshBorderButtonStates();
        refreshLevelButtons();
        updatePreview();
    }

    private void construirUI() {
        JLabel titulo = new JLabel("Seleccione el modo de juego");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(new Color(30, 80, 180));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(5, 1, 0, 10));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

        // Fila de modos
        JPanel filaModos = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        filaModos.setOpaque(false);
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
        filaSkins.setOpaque(false);
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
        JPanel filaBorde1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        filaBorde1.setOpaque(false);
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
        filaBorde2.setOpaque(false);
        labelBorde2 = new JLabel("Borde P2:  ");
        labelBorde2.setFont(new Font("Arial", Font.BOLD, 16));
        filaBorde2.add(labelBorde2);
        borde2Botones = new JButton[BORDER_NAMES.length];
        for (int i = 0; i < BORDER_NAMES.length; i++) {
            borde2Botones[i] = new JButton(BORDER_NAMES[i]);
            borde2Botones[i].setFont(new Font("Arial", Font.BOLD, 14));
            filaBorde2.add(borde2Botones[i]);
        }
        filaBorde2.setVisible(false);

        // Fila de niveles
        filaLevel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        filaLevel.setOpaque(false);
        JLabel labelLevel = new JLabel("Nivel:  ");
        labelLevel.setFont(new Font("Arial", Font.BOLD, 16));
        filaLevel.add(labelLevel);

        centro.add(filaModos);
        centro.add(filaSkins);
        centro.add(filaBorde1);
        centro.add(filaBorde2);
        centro.add(filaLevel);

        // Preview panel a la derecha
        playerPreview = new PlayerPreviewPanel();
        JPanel previewWrapper = new JPanel(new BorderLayout());
        previewWrapper.setOpaque(false);
        previewWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 40));
        JLabel previewLabel = new JLabel("Tu personaje", SwingConstants.CENTER);
        previewLabel.setFont(new Font("Arial", Font.BOLD, 14));
        previewLabel.setForeground(new Color(30, 80, 180));
        previewWrapper.add(previewLabel, BorderLayout.NORTH);
        previewWrapper.add(playerPreview, BorderLayout.CENTER);

        JPanel split = new JPanel(new BorderLayout());
        split.setOpaque(false);
        split.add(centro, BorderLayout.CENTER);
        split.add(previewWrapper, BorderLayout.EAST);

        add(split, BorderLayout.CENTER);

        // Botones de navegación
        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        filaBotones.setOpaque(false);
        btnVolver = UIFactory.createStyledButton("VOLVER", new Color(0x9A, 0x4B, 0xC1));
        btnJugar = UIFactory.createStyledButton("JUGAR", new Color(0xEB, 0x55, 0x55));
        filaBotones.add(btnVolver);
        filaBotones.add(btnJugar);
        add(filaBotones, BorderLayout.SOUTH);
    }

    private void wireAcciones() {
        btnVolver.addActionListener(e -> host.mostrarExplicacion());

        btnModePlayer.addActionListener(e -> {
            selectedMode = GameMode.PLAYER;
            selectedLevel = 1;
            highlightButton(btnModePlayer, btnModePvsP, btnModePvsM);
            filaBorde2.setVisible(false);
            refreshBorderButtonStates();
            refreshLevelButtons();
            updatePreview();
        });
        btnModePvsP.addActionListener(e -> {
            selectedMode = GameMode.PvsP;
            selectedLevel = 1;
            highlightButton(btnModePvsP, btnModePlayer, btnModePvsM);
            filaBorde2.setVisible(true);
            ensureBordersDiffer();
            refreshBorderButtonStates();
            refreshLevelButtons();
            updatePreview();
        });
        btnModePvsM.addActionListener(e -> {
            selectedMode = GameMode.PvsM;
            selectedLevel = 1;
            highlightButton(btnModePvsM, btnModePlayer, btnModePvsP);
            filaBorde2.setVisible(true);
            ensureBordersDiffer();
            refreshBorderButtonStates();
            refreshLevelButtons();
            updatePreview();
        });

        for (int i = 0; i < BORDER_NAMES.length; i++) {
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
            juego.startGame(selectedLevel);
            host.iniciarJuego();
        });
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
        labelBorde2.setText(isMachine ? "Borde Maquina:  " : "Borde P2:  ");
    }

    private void refreshLevelButtons() {
        // Remove old level buttons (keep the label at index 0)
        while (filaLevel.getComponentCount() > 1) filaLevel.remove(1);
        juego.setGameMode(selectedMode);
        int count = juego.getAvailableLevelCount();
        for (int i = 1; i <= count; i++) {
            final int lvl = i;
            JButton btn = new JButton(String.valueOf(i));
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            if (lvl == selectedLevel) btn.setBackground(new Color(0xEB, 0x55, 0x55));
            btn.addActionListener(e -> {
                selectedLevel = lvl;
                refreshLevelButtons();
            });
            filaLevel.add(btn);
        }
        if (selectedLevel > count) selectedLevel = 1;
        filaLevel.revalidate();
        filaLevel.repaint();
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
            applyBorderButtonStyle(borde1Botones[i], c,
                c.equals(selectedBorder1),
                twoPlayers && c.equals(selectedBorder2));
            applyBorderButtonStyle(borde2Botones[i], c,
                c.equals(selectedBorder2),
                twoPlayers && c.equals(selectedBorder1));
        }
        revalidate();
        repaint();
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
        selected.putClientProperty("flat-selected", Boolean.TRUE);
        selected.repaint();
        for (JButton b : others) {
            b.putClientProperty("flat-selected", Boolean.FALSE);
            b.repaint();
        }
    }
}
