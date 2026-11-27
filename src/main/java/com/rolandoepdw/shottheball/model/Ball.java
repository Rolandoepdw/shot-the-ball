package com.rolandoepdw.shottheball.model;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * A ball on the board. It knows its own play area and bounces off all four
 * edges. Movement is resolved in one pass per tick using dx/dy velocity,
 * avoiding the chained comparisons of the original version.
 */
public final class Ball {

    public static final int MAX_HITS = 5;

    private final Dimension bounds;
    private final int size;
    private double x;
    private double y;
    private double dx;
    private double dy;
    private int remainingHits = MAX_HITS;
    private boolean bouncedOnBottom;

    /** Ball with a random position and velocity inside the area. */
    public Ball(Dimension bounds, int size, Random random) {
        this(bounds, size,
                random.nextInt(Math.max(1, bounds.width - size)),
                random.nextInt(Math.max(1, bounds.height - size)),
                signedSpeed(random),
                signedSpeed(random));
    }

    /** Deterministic constructor, useful for tests. */
    public Ball(Dimension bounds, int size, double x, double y, double dx, double dy) {
        this.bounds = new Dimension(Math.max(1, bounds.width), Math.max(1, bounds.height));
        this.size = Math.max(1, size);
        this.x = clamp(x, 0, Math.max(0, this.bounds.width - this.size));
        this.y = clamp(y, 0, Math.max(0, this.bounds.height - this.size));
        this.dx = dx == 0 ? 1 : dx;
        this.dy = dy == 0 ? 1 : dy;
    }

    private static double signedSpeed(Random random) {
        int speed = 1 + random.nextInt(2);
        return random.nextBoolean() ? speed : -speed;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Advances exactly one step, reflecting the velocity on every reached edge.
     *
     * @return {@code true} if the bottom edge was touched during this step
     */
    public boolean move() {
        bouncedOnBottom = false;
        x += dx;
        y += dy;

        double maxX = bounds.width - size;
        if (maxX <= 0) {
            x = 0;
            dx = -dx;
        } else if (x < 0) {
            x = -x;
            dx = -dx;
        } else if (x > maxX) {
            x = 2 * maxX - x;
            dx = -dx;
        }

        double maxY = bounds.height - size;
        if (maxY <= 0) {
            y = 0;
            dy = -dy;
            bouncedOnBottom = true;
        } else if (y < 0) {
            y = -y;
            dy = -dy;
        } else if (y > maxY) {
            y = 2 * maxY - y;
            dy = -dy;
            bouncedOnBottom = true;
        }
        return bouncedOnBottom;
    }

    /** @return {@code true} when the ball has just been destroyed. */
    public boolean hit() {
        remainingHits = Math.max(0, remainingHits - 1);
        return isDestroyed();
    }

    public boolean isDestroyed() {
        return remainingHits == 0;
    }

    public boolean contains(Point point) {
        return point != null && getShape().contains(point);
    }

    public Ellipse2D getShape() {
        return new Ellipse2D.Double(x, y, size, size);
    }

    public Rectangle getRectangle() {
        return new Rectangle((int) x, (int) y, size, size);
    }

    /** Name of the sprite matching the number of hits received. */
    public String getImageName() {
        return switch (remainingHits) {
            case 5 -> "blue_ball.bmp";
            case 4 -> "green_ball.bmp";
            case 3 -> "yellow_ball.bmp";
            case 2 -> "orange_ball.bmp";
            default -> "red_ball.bmp";
        };
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getDx() { return dx; }
    public double getDy() { return dy; }
    public int getSize() { return size; }
    public int getRemainingHits() { return remainingHits; }
    public boolean isBouncedOnBottom() { return bouncedOnBottom; }
    public Dimension getBounds() { return new Dimension(bounds); }
}
