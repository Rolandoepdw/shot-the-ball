package com.rolandoepdw.shottheball.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Serializable record of one game. It is comparable by descending score
 * (and, stably, by player and duration in case of a tie).
 */
public final class LeaderboardEntry implements Comparable<LeaderboardEntry>, Serializable {

    private static final long serialVersionUID = 2026L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public static final String[] COLUMN_NAMES = {
        "Record", "Nickname", "Kill", "Difficulty", "Duration", "W-L", "Game Date"
    };

    private final String outcome;
    private final String player;
    private final String difficulty;
    private final String date;
    private final int kills;
    private final int durationSeconds;
    private final int score;

    public LeaderboardEntry(String outcome, String player, int kills,
                            String difficulty, int durationSeconds) {
        this.outcome = outcome == null ? "" : outcome;
        this.player = player == null || player.isBlank() ? "Player" : player.trim();
        this.kills = Math.max(0, kills);
        this.difficulty = difficulty == null ? "" : difficulty;
        this.durationSeconds = Math.max(0, durationSeconds);
        this.score = calculateScore(this.outcome, this.difficulty, this.kills, this.durationSeconds);
        this.date = LocalDate.now().format(DATE_FORMAT);
    }

    /** Same formula as the original, but using equals instead of ==. */
    public static int calculateScore(String outcome, String difficulty, int kills, int seconds) {
        int bonus = "Win".equalsIgnoreCase(outcome) ? 1000 : 0;
        if ("EASY".equalsIgnoreCase(difficulty)) {
            return kills - seconds + bonus;
        }
        if ("NORMAL".equalsIgnoreCase(difficulty)) {
            return kills - seconds / 2 + bonus;
        }
        if ("HARD".equalsIgnoreCase(difficulty)) {
            return kills - seconds / 3 + bonus;
        }
        return 0;
    }

    public static String formatDuration(int seconds) {
        int value = Math.max(0, seconds);
        return String.format("%d:%02d:%02d", value / 3600, value / 60 % 60, value % 60);
    }

    @Override
    public int compareTo(LeaderboardEntry other) {
        int result = Integer.compare(other.score, this.score);
        if (result != 0) {
            return result;
        }
        result = this.player.compareToIgnoreCase(other.player);
        if (result != 0) {
            return result;
        }
        return Integer.compare(this.durationSeconds, other.durationSeconds);
    }

    public String[] toRow() {
        return new String[]{
            String.valueOf(score), player, String.valueOf(kills), difficulty,
            formatDuration(durationSeconds), outcome, date
        };
    }

    @Override
    public String toString() {
        return String.join(",", toRow());
    }

    public String getOutcome() { return outcome; }
    public String getPlayer() { return player; }
    public String getDifficulty() { return difficulty; }
    public String getDate() { return date; }
    public int getKills() { return kills; }
    public int getDurationSeconds() { return durationSeconds; }
    public int getScore() { return score; }
}
