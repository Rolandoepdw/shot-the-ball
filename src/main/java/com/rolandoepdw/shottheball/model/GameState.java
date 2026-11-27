package com.rolandoepdw.shottheball.model;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Game state, independent of Swing. One tick is one frame (~60 per second);
 * all lives, kills, ammo and end-of-game logic lives here so it can be tested
 * without a graphical interface.
 */
public final class GameState {

    public enum Status { PLAYING, WON, LOST }
    public enum ShotResult { RELOADED, HIT, DESTROYED, MISS }

    public static final int MAGAZINE_SIZE = 14;
    public static final int MAX_LIVES = 3;
    public static final int WIN_KILLS = 100;
    private static final int TICKS_PER_SECOND = 60;
    private static final int SPAWN_EVERY_TICKS = 120;

    private final Difficulty difficulty;
    private final Dimension bounds;
    private final Random random;
    private final List<Ball> balls = new ArrayList<>();

    private int lives = MAX_LIVES;
    private int kills;
    private int ammo = MAGAZINE_SIZE;
    private int ticks;
    private int elapsedSeconds;
    private int escaped;
    private int spawnCounter;
    private Status status = Status.PLAYING;
    private Point lastShot;

    public GameState(Difficulty difficulty, Dimension bounds) {
        this(difficulty, bounds, new Random());
    }

    public GameState(Difficulty difficulty, Dimension bounds, Random random) {
        this(difficulty, bounds, random, difficulty.getInitialBalls());
    }

    public GameState(Difficulty difficulty, Dimension bounds, Random random, int initialBalls) {
        this.difficulty = difficulty;
        this.bounds = new Dimension(bounds);
        this.random = random;
        for (int i = 0; i < Math.max(0, initialBalls); i++) {
            balls.add(new Ball(this.bounds, difficulty.getBallSize(), random));
        }
    }

    /** Test constructor: starts with exactly the given balls. */
    public GameState(Difficulty difficulty, Dimension bounds, List<Ball> initialBalls) {
        this.difficulty = difficulty;
        this.bounds = new Dimension(bounds);
        this.random = new Random();
        this.balls.addAll(initialBalls);
    }

    /** Advances one frame: moves, removes escapes, spawns and checks the end. */
    public void tick() {
        if (status != Status.PLAYING) {
            return;
        }
        ticks++;
        elapsedSeconds = ticks / TICKS_PER_SECOND;

        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            ball.move();
            if (ball.isBouncedOnBottom()) {
                iterator.remove();
                escaped++;
                lives = Math.max(0, lives - 1);
            }
        }

        if (lives == 0 || escaped >= difficulty.getGameOverBalls()) {
            status = Status.LOST;
            return;
        }
        if (balls.isEmpty() && kills > 0) {
            status = Status.WON;
            return;
        }
        if (balls.isEmpty() || ++spawnCounter >= SPAWN_EVERY_TICKS) {
            spawnCounter = 0;
            balls.add(new Ball(bounds, difficulty.getBallSize(), random));
        }
        if (kills >= WIN_KILLS) {
            status = Status.WON;
        }
    }

    /** Shoots at a point. With an empty magazine, reloads without spending ammo. */
    public ShotResult shoot(Point point) {
        if (status != Status.PLAYING || point == null) {
            return ShotResult.MISS;
        }
        lastShot = new Point(point);
        if (ammo == 0) {
            ammo = MAGAZINE_SIZE;
            return ShotResult.RELOADED;
        }
        ammo--;

        ShotResult result = ShotResult.MISS;
        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            if (ball.contains(point)) {
                boolean destroyed = ball.hit();
                result = destroyed ? ShotResult.DESTROYED : ShotResult.HIT;
                if (destroyed) {
                    iterator.remove();
                    kills++;
                }
            }
        }
        if (balls.isEmpty() && kills > 0) {
            status = Status.WON;
        }
        return result;
    }

    public LeaderboardEntry createEntry(String player) {
        String outcome = status == Status.WON ? "Win" : "Lose";
        return new LeaderboardEntry(outcome, player, kills, difficulty.name(), elapsedSeconds);
    }

    public static String formatTime(int seconds) {
        return LeaderboardEntry.formatDuration(seconds);
    }

    public List<Ball> getBalls() {
        return Collections.unmodifiableList(balls);
    }

    public Difficulty getDifficulty() { return difficulty; }
    public Dimension getBounds() { return new Dimension(bounds); }
    public int getLives() { return lives; }
    public int getKills() { return kills; }
    public int getAmmo() { return ammo; }
    public int getElapsedSeconds() { return elapsedSeconds; }
    public int getEscapedBalls() { return escaped; }
    public Status getStatus() { return status; }
    public Point getLastShot() { return lastShot == null ? null : new Point(lastShot); }
}
