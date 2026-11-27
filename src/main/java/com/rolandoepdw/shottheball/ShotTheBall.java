package com.rolandoepdw.shottheball;

import com.rolandoepdw.shottheball.audio.SoundPlayer;
import com.rolandoepdw.shottheball.model.Leaderboard;
import com.rolandoepdw.shottheball.ui.GameFrame;
import com.rolandoepdw.shottheball.ui.LeaderboardDialog;
import com.rolandoepdw.shottheball.ui.Menu;
import com.rolandoepdw.shottheball.ui.Resources;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/** Entry point of the game. */
public final class ShotTheBall {

    public static final Path LEADERBOARD_PATH =
            Paths.get(System.getProperty("user.home"), ".shottheball", "leaderboard.dat");
    public static final Dimension BOARD_SIZE = new Dimension(900, 600);

    private ShotTheBall() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Leaderboard leaderboard = new Leaderboard(LEADERBOARD_PATH);
            SoundPlayer sounds = new SoundPlayer();
            Menu menu = new Menu();
            JFrame frame = new JFrame("Shot the Ball");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setBackground(Color.DARK_GRAY);
            frame.setIconImage(Resources.loadImage("sb.png"));
            frame.setCursor(Resources.cursor("crosshair.png"));

            menu.setOnPlay(() -> {
                frame.dispose();
                new GameFrame(menu.getPlayerName(), menu.getDifficulty(),
                        BOARD_SIZE, leaderboard, sounds).setVisible(true);
            });
            menu.setOnLeaderboard(() ->
                    new LeaderboardDialog(frame, leaderboard).setVisible(true));

            frame.add(menu);
            frame.pack();
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation((screen.width - frame.getWidth()) / 2,
                    (screen.height - frame.getHeight()) / 2);
            frame.setVisible(true);
        });
    }
}
