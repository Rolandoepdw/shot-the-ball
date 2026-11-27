package com.rolandoepdw.shottheball.model;

import java.awt.Dimension;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BallTest {

    private static final Dimension AREA = new Dimension(200, 200);

    @Test
    void reboundsOffLeftEdge() {
        Ball ball = new Ball(AREA, 20, 1, 100, -2, 0);

        ball.move();

        assertEquals(1.0, ball.getX());
        assertTrue(ball.getDx() > 0, "The x velocity must be reversed");
    }

    @Test
    void reboundsOffRightEdge() {
        Ball ball = new Ball(AREA, 20, 180, 100, 2, 0);

        ball.move();

        assertEquals(178.0, ball.getX());
        assertTrue(ball.getDx() < 0, "The x velocity must be reversed");
    }

    @Test
    void reboundsOffTopEdge() {
        Ball ball = new Ball(AREA, 20, 100, 1, 0, -2);

        ball.move();

        assertEquals(1.0, ball.getY());
        assertTrue(ball.getDy() > 0, "The y velocity must be reversed");
    }

    @Test
    void reboundsOffBottomEdge() {
        Ball ball = new Ball(AREA, 20, 100, 180, 0, 2);

        boolean touchedBottom = ball.move();

        assertEquals(178.0, ball.getY());
        assertTrue(ball.getDy() < 0, "The y velocity must be reversed");
        assertTrue(touchedBottom);
        assertTrue(ball.isBouncedOnBottom());
    }

    @Test
    void advancesOnlyOncePerTick() {
        Ball ball = new Ball(AREA, 20, 100, 100, 2, 3);

        ball.move();

        assertEquals(102.0, ball.getX());
        assertEquals(103.0, ball.getY());
    }

    @Test
    void fiveHitsDestroyBallAndChangeSprite() {
        Ball ball = new Ball(AREA, 20, 100, 100, 1, 1);

        assertEquals("blue_ball.bmp", ball.getImageName());
        assertFalse(ball.hit());
        assertEquals("green_ball.bmp", ball.getImageName());
        ball.hit();
        assertEquals("yellow_ball.bmp", ball.getImageName());
        ball.hit();
        assertEquals("orange_ball.bmp", ball.getImageName());
        ball.hit();
        assertEquals("red_ball.bmp", ball.getImageName());
        assertTrue(ball.hit());
        assertTrue(ball.isDestroyed());
        assertEquals(0, ball.getRemainingHits());
    }
}
