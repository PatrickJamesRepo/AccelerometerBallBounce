package com.example.m03_bounce;

import android.graphics.Canvas;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PadManager {
    private List<Pad> pads;
    private Random random;

    // Constructor to initialize the pads list and random generator
    public PadManager() {
        pads = new ArrayList<>();
        random = new Random();
    }

    // Method to add a new pad at a random position on the screen
    public void addPad(int screenWidth, int screenHeight) {
        float startX = random.nextInt(screenWidth - 150);
        float startY = screenHeight / 2 + random.nextInt(screenHeight / 2);
        pads.add(new Pad(startX, startY, 150, 30, 0xFF808080));
    }

    // Method to update and draw all pads on the canvas
    public void updateAndDrawPads(Canvas canvas) {
        int screenWidth = canvas.getWidth();
        for (Pad pad : pads) {
            pad.update(screenWidth);
            pad.draw(canvas);
        }
    }

    // Method to handle collisions between balls and pads
    public boolean handleCollisions(List<Ball> balls, ScoreManager scoreManager, int screenWidth, int screenHeight, BouncingBallView.OnScoreChangeListener scoreChangeListener) {
        boolean collisionOccurred = false;
        for (Ball ball : balls) {
            for (Pad pad : pads) {
                if (pad.isVisible() && ball.collidesWith(pad)) {
                    scoreManager.incrementScore();
                    collisionOccurred = true;

                    // Notify listener about score change
                    if (scoreChangeListener != null) {
                        scoreChangeListener.onScoreChange(scoreManager.getScore());
                    }
                    // Reverse ball direction on collision, hide pade , reposition pad to new random location
                    ball.reverseY();
                    pad.hide();
                    pad.reappear(screenWidth, screenHeight);
                }
            }
        }
        return collisionOccurred;
    }

    // Method to get the list of pads
    public List<Pad> getPads() {
        return pads;
    }
}
