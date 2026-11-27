package com.rolandoepdw.shottheball.ui;

import com.rolandoepdw.shottheball.audio.SoundPlayer;
import com.rolandoepdw.shottheball.model.Difficulty;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/** Original launcher menu: title image, three option panels and a welcome footer. */
public final class Menu extends JPanel {

    private static final Font PANEL_FONT = new Font("sans-serif", Font.PLAIN, 18);
    private static final Color BACKGROUND = Color.DARK_GRAY;

    private final SoundPlayer sounds = new SoundPlayer();
    private final JTextField nicknameField = new JTextField(12);
    private final JLabel informationLabel;
    private Difficulty selectedDifficulty;

    private Runnable onPlay = () -> { };
    private Runnable onLeaderboard = () -> { };

    public Menu() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        ImagePanel title = new ImagePanel(Resources.loadImage("title.bmp"), new Dimension(558, 166));
        add(title, BorderLayout.NORTH);

        JPanel optionPanels = new JPanel(new GridLayout(1, 3));
        optionPanels.setBackground(BACKGROUND);
        optionPanels.add(createMenuPanel());
        optionPanels.add(createDifficultyPanel());
        optionPanels.add(createUsersPanel());
        add(optionPanels, BorderLayout.CENTER);

        informationLabel = new JLabel("Welcome to Shot the Ball. By Rolando E. Pérez Domínguez");
        informationLabel.setForeground(Color.BLACK);
        informationLabel.setBackground(BACKGROUND);
        informationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        informationLabel.setEnabled(true);
        add(informationLabel, BorderLayout.SOUTH);
    }

    private JPanel createMenuPanel() {
        JPanel panel = titledPanel("Menu");
        addButton(panel, "Play", e -> {
            sounds.play("shot.wav");
            if (nicknameField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Enter your nickname.");
                return;
            }
            if (selectedDifficulty == null) {
                JOptionPane.showMessageDialog(this, "Select the difficulty.");
                return;
            }
            onPlay.run();
        });
        addButton(panel, "Exit", e ->
                SwingUtilities.getWindowAncestor(this).dispose());
        return panel;
    }

    private JPanel createDifficultyPanel() {
        JPanel panel = titledPanel("Difficulty");
        for (Difficulty difficulty : Difficulty.values()) {
            addButton(panel, difficulty.getDisplayName(), e -> {
                selectedDifficulty = difficulty;
                informationLabel.setText(difficulty.toString());
                sounds.play("shot.wav");
            });
        }
        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = titledPanel("Users");
        nicknameField.setBackground(Color.DARK_GRAY);
        nicknameField.setForeground(Color.BLACK.darker());
        nicknameField.setAlignmentX(CENTER_ALIGNMENT);
        nicknameField.setHorizontalAlignment((int) CENTER_ALIGNMENT);
        nicknameField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK),
                "Nickname", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("sans-serif", Font.PLAIN, 12), Color.BLACK));
        nicknameField.setFont(new Font("sans-serif", Font.PLAIN, 15));
        panel.add(nicknameField);
        panel.add(Box.createVerticalStrut(7));
        addButton(panel, "Enter", e -> sounds.play("shot.wav"));
        return panel;
    }

    private JPanel titledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK),
                title, TitledBorder.CENTER, TitledBorder.TOP, PANEL_FONT, Color.BLACK));
        return panel;
    }

    private void addButton(JPanel panel, String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.BLACK.darker());
        button.addActionListener(listener);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setBorder(null);
        button.setRolloverEnabled(true);
        button.setFont(PANEL_FONT);
        panel.add(button);
        panel.add(Box.createVerticalStrut(7));
    }

    public void setOnPlay(Runnable onPlay) { this.onPlay = onPlay; }

    /** Kept as a hook for callers that want to open the leaderboard from the launcher. */
    public void setOnLeaderboard(Runnable onLeaderboard) { this.onLeaderboard = onLeaderboard; }

    public String getPlayerName() {
        String text = nicknameField.getText();
        return text == null || text.isBlank() ? "" : text.trim();
    }

    public Difficulty getDifficulty() {
        return selectedDifficulty == null ? Difficulty.NORMAL : selectedDifficulty;
    }
}
