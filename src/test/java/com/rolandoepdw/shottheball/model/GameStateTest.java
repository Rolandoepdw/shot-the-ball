package com.rolandoepdw.shottheball.model;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameStateTest {

    private static final Dimension AREA = new Dimension(200, 200);
    private static final Point CENTER = new Point(110, 110);

    @Test
    void losesLifeAndRemovesBallTouchingBottom() {
        Ball ball = new Ball(AREA, 20, 100, 179, 0, 2);
        GameState state = new GameState(Difficulty.EASY, AREA, List.of(ball));

        state.tick();

        assertEquals(GameState.MAX_LIVES - 1, state.getLives());
        assertEquals(1, state.getEscapedBalls());
        assertTrue(state.getBalls().stream().noneMatch(candidate -> candidate == ball),
                "The escaped ball must disappear from the board");
        assertEquals(1, state.getBalls().size(), "The game spawns a replacement");
        assertEquals(GameState.Status.PLAYING, state.getStatus());
    }

    @Test
    void shotsDestroyBallAndAddKills() {
        Ball ball = new Ball(AREA, 20, 100, 100, 0, 0);
        GameState state = new GameState(Difficulty.EASY, AREA, List.of(ball));

        for (int i = 0; i < Ball.MAX_HITS - 1; i++) {
            assertEquals(GameState.ShotResult.HIT, state.shoot(CENTER));
        }
        assertEquals(GameState.ShotResult.DESTROYED, state.shoot(CENTER));

        assertEquals(1, state.getKills());
        assertTrue(state.getBalls().isEmpty());
        assertEquals(GameState.Status.WON, state.getStatus());
        assertEquals(GameState.MAGAZINE_SIZE - Ball.MAX_HITS, state.getAmmo());
    }

    @Test
    void magazineRunsOutAndNextClickReloads() {
        GameState state = new GameState(Difficulty.EASY, AREA, List.of());

        for (int i = 0; i < GameState.MAGAZINE_SIZE; i++) {
            assertEquals(GameState.ShotResult.MISS, state.shoot(CENTER));
        }
        assertEquals(0, state.getAmmo());

        assertEquals(GameState.ShotResult.RELOADED, state.shoot(CENTER));
        assertEquals(GameState.MAGAZINE_SIZE, state.getAmmo());
    }

    @Test
    void eachDifficultySpawnsItsInitialBalls() {
        assertEquals(10, new GameState(Difficulty.EASY, AREA).getBalls().size());
        assertEquals(20, new GameState(Difficulty.NORMAL, AREA).getBalls().size());
        assertEquals(30, new GameState(Difficulty.HARD, AREA).getBalls().size());
    }

    @Test
    void losingAllLivesEndsGame() {
        GameState state = new GameState(Difficulty.EASY, AREA, List.of(
                new Ball(AREA, 20, 40, 179, 0, 2),
                new Ball(AREA, 20, 100, 179, 0, 2),
                new Ball(AREA, 20, 160, 179, 0, 2)));

        state.tick();

        assertEquals(0, state.getLives());
        assertEquals(3, state.getEscapedBalls());
        assertEquals(GameState.Status.LOST, state.getStatus());
    }
}
