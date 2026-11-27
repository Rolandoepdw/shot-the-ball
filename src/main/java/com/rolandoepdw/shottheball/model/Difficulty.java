package com.rolandoepdw.shottheball.model;

/**
 * Difficulty levels of the game. The values are the ones from the original.
 */
public enum Difficulty {
    EASY("Easy", 10, 35, 25),
    NORMAL("Normal", 20, 30, 30),
    HARD("Hard", 30, 30, 35);

    private final String displayName;
    private final int initialBalls;
    private final int ballSize;
    private final int gameOverBalls;

    Difficulty(String displayName, int initialBalls, int ballSize, int gameOverBalls) {
        this.displayName = displayName;
        this.initialBalls = initialBalls;
        this.ballSize = ballSize;
        this.gameOverBalls = gameOverBalls;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getInitialBalls() {
        return initialBalls;
    }

    public int getBallSize() {
        return ballSize;
    }

    /** Number of escaped balls from which the game is lost. */
    public int getGameOverBalls() {
        return gameOverBalls;
    }

    @Override
    public String toString() {
        return "*** Difficulty: " + displayName + " / Initial balls: " + initialBalls
                + " / Ball size: " + ballSize + "x" + ballSize + " / Game Over: "
                + gameOverBalls + " balls ***";
    }
}
