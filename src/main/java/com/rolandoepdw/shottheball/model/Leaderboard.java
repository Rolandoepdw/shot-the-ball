package com.rolandoepdw.shottheball.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Persistent leaderboard. Everything is written again at once, which avoids
 * duplicate headers and corrupt files when adding new entries.
 */
public final class Leaderboard {

    private final Path file;

    public Leaderboard(Path file) {
        this.file = file;
    }

    public Path getFile() {
        return file;
    }

    public synchronized void add(LeaderboardEntry entry) throws IOException {
        List<LeaderboardEntry> entries = readAll();
        entries.add(entry);
        save(entries);
    }

    public synchronized void save(List<LeaderboardEntry> entries) throws IOException {
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(file)))) {
            for (LeaderboardEntry entry : entries) {
                out.writeObject(entry);
            }
        }
    }

    public synchronized List<LeaderboardEntry> readAll() throws IOException {
        List<LeaderboardEntry> entries = new ArrayList<>();
        if (!Files.exists(file) || Files.size(file) == 0) {
            return entries;
        }
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(file)))) {
            while (true) {
                try {
                    entries.add((LeaderboardEntry) in.readObject());
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Could not read a leaderboard entry", e);
        }
        return entries;
    }

    /** Returns a copy sorted from best to worst score. */
    public List<LeaderboardEntry> ranked() throws IOException {
        List<LeaderboardEntry> entries = new ArrayList<>(readAll());
        entries.sort(Comparator.naturalOrder());
        return entries;
    }

    public List<LeaderboardEntry> top(int limit) throws IOException {
        List<LeaderboardEntry> ranked = ranked();
        return new ArrayList<>(ranked.subList(0, Math.max(0, Math.min(limit, ranked.size()))));
    }
}
