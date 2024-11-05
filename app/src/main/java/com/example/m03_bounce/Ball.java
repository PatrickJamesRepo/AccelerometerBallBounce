package com.example.m03_bounce;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball {
    private String id;  // Unique identifier for the ball
    private int color;  // Color of the ball
    private float x, y;  // Current position of the ball
    private float vx, vy;  // Current velocity of the ball in the x and y directions
    private static final float RADIUS = 30;  // Radius of the ball
    private static final float GRAVITY = 0.5f;  // Gravity affecting the ball
    private static final float BOUNCE_FACTOR = 0.6f;  // Bounce factor for collisions
    private static final float DAMPING_FACTOR = 0.98f;  // Damping factor to slow down the ball over time


    // Constructor to initialize the ball's properties
    public Ball(int color, float x, float y, float vx, float vy) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    // Updates the position of the ball and handles collisions with the box boundaries
    public void updatePosition(Box box) {
        vy += GRAVITY;
        vx *= DAMPING_FACTOR;
        vy *= DAMPING_FACTOR;

        x += vx;
        y += vy;

        // Handle collisions with the box walls (left/right)
        if (x - RADIUS < box.left || x + RADIUS > box.right) {
            vx = -vx * BOUNCE_FACTOR;
            x = (x - RADIUS < box.left) ? box.left + RADIUS : box.right - RADIUS;
        }

        // Handle collisions with the box walls (top/bottom)
        if (y - RADIUS < box.top || y + RADIUS > box.bottom) {
            vy = -vy * BOUNCE_FACTOR;
            y = (y - RADIUS < box.top) ? box.top + RADIUS : box.bottom - RADIUS;
        }
    }

    // Draws the ball on the canvas
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawCircle(x, y, RADIUS, paint);
    }

    // Applies acceleration to the ball's velocity
    public void applyAcceleration(double ax, double ay) {
        vx += ax;
        vy += ay;
    }

    // Checks if the ball collides with the given pad
    public boolean collidesWith(Pad pad) {
        return (x + RADIUS > pad.getX() && x - RADIUS < pad.getX() + pad.getWidth()) &&
                (y + RADIUS > pad.getY() && y - RADIUS < pad.getY() + pad.getHeight());
    }

    // Reverses the y-velocity of the ball (used for bouncing)
    public void reverseY() {
        vy = -vy;
    }

    // Serializes the ball's data into a string for saving purposes
    @Override
    public String toString() {
        return color + "," + x + "," + y + "," + vx + "," + vy;
    }

    // Creates a Ball object from a serialized string
    public static Ball fromString(String data) {
        try {
            String[] parts = data.split(",");
            if (parts.length != 5) return null;

            int color = Integer.parseInt(parts[0]);
            float x = Float.parseFloat(parts[1]);
            float y = Float.parseFloat(parts[2]);
            float vx = Float.parseFloat(parts[3]);
            float vy = Float.parseFloat(parts[4]);

            return new Ball(color, x, y, vx, vy);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
