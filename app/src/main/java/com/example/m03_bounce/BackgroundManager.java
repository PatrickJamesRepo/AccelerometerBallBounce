package com.example.m03_bounce;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;

// Class to manage the background images for the game, including day, dusk/dawn, and night.
public class BackgroundManager {
    private Bitmap dayBitmap, duskDawnBitmap, nightBitmap; // Bitmaps representing different backgrounds
    private Bitmap currentBackgroundBitmap; // Currently displayed background bitmap
    private int currentBackgroundIndex = 0; // Index to keep track of the current background
    private Handler backgroundHandler = new Handler(); // Handler for managing background cycling

    // Constructor to initialize the background bitmaps from resources and start the background cycle.
    public BackgroundManager(Context context) {
        // Load the day, dusk/dawn, and night bitmaps from resources
        dayBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.day);
        duskDawnBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.duskdawn);
        nightBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.night);
        currentBackgroundBitmap = dayBitmap; // Set the initial background to day
        startBackgroundCycle(); // Start cycling through backgrounds
    }

    // Method to start the background cycle that changes every 5 seconds.
    private void startBackgroundCycle() {
        backgroundHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cycleBackground(); // Cycle to the next background
                backgroundHandler.postDelayed(this, 5000); // Schedule the next background change in 5 seconds
            }
        }, 5000);
    }

    // Method to cycle through the backgrounds (day, dusk/dawn, night).
    private void cycleBackground() {
        currentBackgroundIndex = (currentBackgroundIndex + 1) % 3; // Increment the index and wrap around after 3
        switch (currentBackgroundIndex) {
            case 0:
                currentBackgroundBitmap = dayBitmap; // Set background to day
                break;
            case 1:
                currentBackgroundBitmap = duskDawnBitmap; // Set background to dusk/dawn
                break;
            case 2:
                currentBackgroundBitmap = nightBitmap; // Set background to night
                break;
        }
    }

    // Method to draw the current background onto the given canvas.
    public void drawBackground(Canvas canvas) {
        if (currentBackgroundBitmap != null) {
            // Draw the current background bitmap, scaling it to fit the canvas
            canvas.drawBitmap(currentBackgroundBitmap, null, canvas.getClipBounds(), null);
        } else {
            // If no bitmap is available, fill the canvas with a white color
            canvas.drawColor(android.graphics.Color.WHITE);
        }
    }
}
