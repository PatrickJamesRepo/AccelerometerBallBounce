package com.example.m03_bounce;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.BlurMaskFilter;

public class Pad {
    private float x, y;
    private float width, height;
    private boolean visible;
    private Paint bodyPaint, glowPaint, windowPaint, topBodyPaint;
    private float speedX;

    // Constructor to initialize the UFO's properties
    public Pad(float x, float y, float width, float height, int color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visible = true;

        // Initialize paint objects for different parts of the pad
        topBodyPaint = new Paint();
        topBodyPaint.setColor(0xFF808080);
        bodyPaint = new Paint();
        bodyPaint.setColor(0xFF666666);
        glowPaint = new Paint();
        glowPaint.setColor(0x80FFFF00);
        glowPaint.setMaskFilter(new BlurMaskFilter(40, BlurMaskFilter.Blur.NORMAL));
        windowPaint = new Paint();
        windowPaint.setColor(0xFF444444);
        speedX = 5;
    }

    // Update the pad's position, bouncing off screen edges
    public void update(int screenWidth) {
        x += speedX;
        // Reverse direction if the pad hits the screen boundaries
        if (x < 0 || x + width > screenWidth) {
            speedX = -speedX;
        }
    }

    // Draw the pad on the canvas
    public void draw(Canvas canvas) {
        if (visible) {
            // Draw glow effect
            RectF glowOval = new RectF(x - 20, y + height / 2, x + width + 20, y + height * 1.5f);
            canvas.drawOval(glowOval, glowPaint);

            // Draw base oval of the pad
            RectF baseOval = new RectF(x + width / 10, y + height / 4, x + width - width / 10, y + height);
            canvas.drawOval(baseOval, bodyPaint);

            // Draw top oval of the pad
            RectF topOval = new RectF(x, y, x + width, y + height / 2);
            canvas.drawOval(topOval, topBodyPaint);

            // Draw windows on the pad
            float windowRadius = width / 10;
            float windowSpacing = width / 4;

            for (int i = 0; i < 3; i++) {
                float windowX = x + windowSpacing * (i + 1) - windowRadius / 2;
                float windowY = y + height / 3;
                canvas.drawCircle(windowX, windowY, windowRadius, windowPaint);
            }
        }
    }

    // Check if the pad is visible
    public boolean isVisible() {
        return visible;
    }

    // Hide the pad
    public void hide() {
        visible = false;
    }

    // Reposition the pad randomly and make it visible again
    public void reappear(int screenWidth, int screenHeight) {
        x = (float) (Math.random() * (screenWidth - width));
        y = (float) (Math.random() * (screenHeight - height));
        visible = true;
    }

    // Getters for the pad's position and dimensions
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
