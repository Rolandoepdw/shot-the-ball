package com.rolandoepdw.shottheball.ui;

import com.rolandoepdw.shottheball.audio.SoundPlayer;
import com.rolandoepdw.shottheball.model.Ball;
import com.rolandoepdw.shottheball.model.GameState;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;

/** Game board: draws balls, HUD and the gunshot flash. */
public final class GamePanel extends JPanel {

    private final GameState state;
    private final SoundPlayer sounds;
    private final Map<String, Image> sprites = new HashMap<>();
    private int shotFlash;

    public GamePanel(GameState state, SoundPlayer sounds) {
        this.state = state;
        this.sounds = sounds;
        setPreferredSize(state.getBounds());
        setBackground(Color.DARK_GRAY);
        setCursor(Resources.cursor("crosshair.png"));
        for (String name : new String[]{
                "blue_ball.bmp", "green_ball.bmp", "yellow_ball.bmp",
                "orange_ball.bmp", "red_ball.bmp"}) {
            sprites.put(name, Resources.loadImage(name));
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                GameState.ShotResult result = state.shoot(event.getPoint());
                if (result == GameState.ShotResult.RELOADED) {
                    sounds.play("shells.wav");
                    sounds.play("reload.wav");
                } else {
                    sounds.play("shot.wav");
                    if (result != GameState.ShotResult.MISS) {
                        shotFlash = 1;
                    }
                }
                repaint();
            }
        });
    }

    /** Called by the Swing timer on the EDT. */
    public void tickFlash() {
        if (shotFlash > 0) {
            shotFlash--;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            for (Ball ball : state.getBalls()) {
                Ellipse2D shape = ball.getShape();
                Image sprite = sprites.get(ball.getImageName());
                if (sprite != null) {
                    g2.drawImage(sprite, (int) shape.getX(), (int) shape.getY(),
                            (int) shape.getWidth(), (int) shape.getHeight(), this);
                } else {
                    g2.setColor(Color.RED);
                    g2.fill(shape);
                }
            }

            if (shotFlash > 0 && state.getLastShot() != null) {
                Point target = state.getLastShot();
                int originX = getWidth() / 2;
                int originY = getHeight() - 30;
                for (int i = 0; i < 10; i++) {
                    g2.setColor(Color.ORANGE.darker());
                    g2.drawLine(originX - 2, originY, target.x, target.y - 2);
                    g2.drawLine(originX - 1, originY, target.x + 1, target.y - 1);
                    g2.setColor(Color.RED);
                    g2.drawLine(originX, originY, target.x + 2, target.y);
                    g2.setColor(Color.BLACK);
                    g2.drawLine(originX + 1, originY, target.x + 3, target.y + 1);
                    g2.drawLine(originX + 2, originY, target.x + 4, target.y + 2);
                }
            }

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 15));
            g2.drawString("Lives: " + state.getLives(), 14, 24);
            g2.drawString("Kills: " + state.getKills(), 110, 24);
            g2.drawString("Ammo: " + state.getAmmo() + "/" + GameState.MAGAZINE_SIZE, 210, 24);
            g2.drawString("Time: " + GameState.formatTime(state.getElapsedSeconds()), 350, 24);
            g2.drawString("Difficulty: " + state.getDifficulty().getDisplayName(), 510, 24);
        } finally {
            g2.dispose();
        }
    }
}
