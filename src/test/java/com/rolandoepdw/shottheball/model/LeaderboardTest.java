package com.rolandoepdw.shottheball.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeaderboardTest {

    @TempDir
    Path directory;

    @Test
    void sortsFromHighestToLowestScore() throws IOException {
        Leaderboard board = new Leaderboard(directory.resolve("ranking.dat"));
        LeaderboardEntry loser = new LeaderboardEntry("Lose", "Ana", 10, "EASY", 10);
        LeaderboardEntry normalWinner = new LeaderboardEntry("Win", "Beto", 20, "NORMAL", 10);
        LeaderboardEntry easyWinner = new LeaderboardEntry("Win", "Cris", 30, "EASY", 5);

        board.add(loser);
        board.add(normalWinner);
        board.add(easyWinner);

        List<LeaderboardEntry> ranked = board.ranked();
        assertEquals(3, ranked.size());
        assertEquals("Cris", ranked.get(0).getPlayer());
        assertEquals("Beto", ranked.get(1).getPlayer());
        assertEquals("Ana", ranked.get(2).getPlayer());
        assertTrue(easyWinner.compareTo(normalWinner) < 0);
    }

    @Test
    void persistsEntriesAcrossInstances() throws IOException {
        Path file = directory.resolve("subdir").resolve("leaderboard.dat");
        Leaderboard first = new Leaderboard(file);
        first.add(new LeaderboardEntry("Win", "Rolando", 42, "HARD", 60));
        first.add(new LeaderboardEntry("Lose", "Guest", 7, "EASY", 30));

        Leaderboard second = new Leaderboard(file);
        List<LeaderboardEntry> stored = second.readAll();
        assertEquals(2, stored.size());
        assertEquals("Rolando", second.top(1).get(0).getPlayer());
        assertEquals(42, stored.stream()
                .filter(e -> "Rolando".equals(e.getPlayer()))
                .findFirst().orElseThrow().getKills());
        assertEquals("0:01:00", LeaderboardEntry.formatDuration(60));
    }
}
