package com.rolandoepdw.shottheball.ui;

import com.rolandoepdw.shottheball.audio.SoundPlayer;
import com.rolandoepdw.shottheball.model.Difficulty;
import com.rolandoepdw.shottheball.model.GameState;
import com.rolandoepdw.shottheball.model.Leaderboard;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Game window. The loop uses javax.swing.Timer: it runs on the EDT. */
public final class GameFrame extends JFrame {

    private static final Logger LOG = Logger.getLogger(GameFrame.class.getName());

    public GameFrame(String player, Difficulty difficulty, Dimension size,
                     Leaderboard leaderboard, SoundPlayer sounds) {
        super("Shot the Ball - " + difficulty.getDisplayName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setAlwaysOnTop(true);
        setBackground(Color.DARK_GRAY);
        setIconImage(Resources.loadImage("sb.png"));
        setCursor(Resources.cursor("crosshair.png"));

        Dimension fallback = size == null ? new Dimension(900, 600) : size;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension playArea = new Dimension(
                screen.width > 0 ? screen.width + 16 : Math.max(1, fallback.width),
                screen.height > 0 ? screen.height + 10 : Math.max(1, fallback.height));

        GameState state = new GameState(difficulty, playArea);
        GamePanel panel = new GamePanel(state, sounds);
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        sounds.play("start.wav");

        Timer timer = new Timer(16, event -> {
            state.tick();
            panel.tickFlash();
            panel.repaint();
            if (state.getStatus() != GameState.Status.PLAYING) {
                ((Timer) event.getSource()).stop();
                try {
                    leaderboard.add(state.createEntry(player));
                } catch (IOException e) {
                    LOG.log(Level.WARNING, "Could not save the leaderboard entry", e);
                }
                String outcome = state.getStatus() == GameState.Status.WON ? "Win" : "Lose";
                dispose();
                SwingUtilities.invokeLater(() ->
                        new LeaderboardDialog(null, leaderboard, outcome).setVisible(true));
            }
        });
        timer.setInitialDelay(0);
        timer.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                timer.stop();
            }
        });

        setBounds(-8, 0, playArea.width, playArea.height);
    }
}
