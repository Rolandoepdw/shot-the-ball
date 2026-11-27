package com.rolandoepdw.shottheball.ui;

import com.rolandoepdw.shottheball.audio.SoundPlayer;
import com.rolandoepdw.shottheball.model.Leaderboard;
import com.rolandoepdw.shottheball.model.LeaderboardEntry;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

/** Window with the historical leaderboard table. */
public final class LeaderboardDialog extends JDialog {

    private static final int WIDTH = 475;
    private static final int HEIGHT = 600;

    public LeaderboardDialog(Frame owner, Leaderboard leaderboard) {
        this(owner, leaderboard, "Win");
    }

    public LeaderboardDialog(Frame owner, Leaderboard leaderboard, String outcome) {
        super(owner, "Leaderboard", true);
        setLayout(new BorderLayout(3, 3));
        setBackground(Color.DARK_GRAY);
        setCursor(Resources.cursor("crosshair.png"));
        setResizable(false);
        setAlwaysOnTop(true);

        String picture = "Win".equalsIgnoreCase(outcome) ? "win.bmp" : "game_over.bmp";
        ImagePanel header = new ImagePanel(Resources.loadImage(picture), new Dimension(475, 141));
        add(header, BorderLayout.NORTH);

        try {
            List<LeaderboardEntry> entries = leaderboard.ranked();
            Vector<Vector<String>> rows = new Vector<>();
            for (LeaderboardEntry entry : entries) {
                Vector<String> row = new Vector<>();
                row.addAll(List.of(entry.toRow()));
                rows.add(row);
            }
            JTable table = new JTable(rows, new Vector<>(List.of(LeaderboardEntry.COLUMN_NAMES)));
            table.setEnabled(false);
            table.setRowHeight(22);
            table.setShowVerticalLines(false);
            table.setShowHorizontalLines(false);
            TableColumnModel columns = table.getColumnModel();
            int[] widths = {30, 85, 20, 35, 35, 20, 80};
            for (int i = 0; i < widths.length; i++) {
                columns.getColumn(i).setPreferredWidth(widths[i]);
            }
            JScrollPane scroll = new JScrollPane(table);
            scroll.setBackground(Color.WHITE);
            scroll.setPreferredSize(new Dimension(475, 300));
            add(scroll, BorderLayout.CENTER);
        } catch (IOException e) {
            add(new JLabel("Could not read the leaderboard file: " + e.getMessage()),
                    BorderLayout.CENTER);
        }

        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        add(close, BorderLayout.SOUTH);

        new SoundPlayer().play("leaderboard.wav");

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screen.width - WIDTH) / 2, (screen.height - HEIGHT) / 2, WIDTH, HEIGHT);
    }
}
