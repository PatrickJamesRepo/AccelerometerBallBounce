package com.example.m03_bounce;


import android.graphics.Canvas;
import java.util.ArrayList;
import java.util.List;

public class BallManager {
    private List<Ball> balls;  // List to store all balls being managed

    // Constructor to initialize the list of balls
    public BallManager() {
        balls = new ArrayList<>();
    }

    // Adds a new ball to the list with the given properties
    public void addBall(int color, float x, float y, float vx, float vy) {
        balls.add(new Ball(color, x, y, vx, vy));
    }

    // Updates the position of all balls based on their velocity and the given box boundaries
    public void updateBallPositions(Box box) {
        for (Ball ball : balls) {
            ball.updatePosition(box);
        }
    }

    // Draws all balls onto the provided canvas
    public void drawBalls(Canvas canvas) {
        for (Ball ball : balls) {
            ball.draw(canvas);
        }
    }

    // Returns the list of all balls
    public List<Ball> getBalls() {
        return balls;
    }

    // Clears all balls from the list
    public void clearBalls() {
        balls.clear();
    }

    // Applies acceleration to all balls in the list
    public void applyAcceleration(double ax, double ay) {
        for (Ball ball : balls) {
            ball.applyAcceleration(ax, ay);
        }
    }

    // Adds an existing Ball object to the list
    public void addBall(Ball ball) {
        balls.add(ball);
    }
}

