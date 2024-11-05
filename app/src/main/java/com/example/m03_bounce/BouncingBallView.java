package com.example.m03_bounce;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

/**
 * Custom view for managing the bouncing ball game, including drawing elements, handling interactions, and managing the game state.
 * Created by Russ on 08/04/2014.
 */
public class BouncingBallView extends View implements SensorEventListener {
    private BackgroundManager backgroundManager;  // Manages the background of the view
    private PadManager padManager;  // Manages the pads in the game
    private BallManager ballManager;  // Manages the balls in the game
    public ScoreManager scoreManager;  // Manages the score of the game
    private VelocityTracker velocityTracker;  // Tracks the velocity of touch gestures
    private SharedPreferences sharedPreferences;  // SharedPreferences to save and load game state

    // Keys for saving game state
    private static final String PREFS_NAME = "BouncingBallPrefs";
    private static final String BALLS_LAUNCHED_KEY = "ballsLaunched";
    private static final String SCORE_KEY = "score";
    private static final String BALL_COUNT_KEY = "ballCount";
    private static final String TAG = "BouncingBallView";

    public int ballsLaunched;  // Number of balls launched
    private OnScoreChangeListener scoreChangeListener;  // Listener for score changes

    // Constructor to initialize the view with context and attributes
    public BouncingBallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        backgroundManager = new BackgroundManager(context);
        ballManager = new BallManager();
        scoreManager = new ScoreManager(context);
        padManager = new PadManager();
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        loadGameState();  // Load the saved game state
    }

    // Called when the size of the view changes, used to add pads if they are not present
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (padManager.getPads().isEmpty()) {
            for (int i = 0; i < 3; i++) {
                padManager.addPad(w, h);
                Log.d(TAG, "Pad added at size: " + w + "x" + h);
            }
        }
    }

    // Draws the background, pads, and balls on the canvas
    @Override
    protected void onDraw(Canvas canvas) {
        backgroundManager.drawBackground(canvas);
        padManager.updateAndDrawPads(canvas);
        ballManager.updateBallPositions(new Box(0, 0, getWidth(), getHeight()));
        ballManager.drawBalls(canvas);

        // Handle collisions between pads and balls
        if (padManager.handleCollisions(ballManager.getBalls(), scoreManager, getWidth(), getHeight(), scoreChangeListener)) {
            // Collision handling logic can be added here if needed
        }

        // Request to redraw the view
        postInvalidateOnAnimation();
    }

    // Handles touch events to add balls to the game
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                velocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (velocityTracker != null) {
                    velocityTracker.addMovement(event);
                    velocityTracker.computeCurrentVelocity(1000);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (velocityTracker != null) {
                    float velocityX = velocityTracker.getXVelocity();
                    float velocityY = velocityTracker.getYVelocity();

                    // Launch a ball if the vertical velocity is significant
                    if (Math.abs(velocityY) > 500) {
                        float x = event.getX();
                        float y = getHeight() - 50;
                        float speedX = velocityX / 75;
                        float speedY = velocityY / -75;

                        ballManager.addBall(0xFFFF0000, x, y, speedX, speedY);
                        ballsLaunched++;
                        saveGameState();  // Save the game state
                    }

                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                break;
        }
        postInvalidateOnAnimation();
        return true;
    }

    // Saves the current game state to SharedPreferences
    public void saveGameState() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(BALLS_LAUNCHED_KEY, ballsLaunched);
        editor.putInt(SCORE_KEY, scoreManager.getScore());
        saveAllBalls(editor);
        boolean success = editor.commit();
        Log.d(TAG, "Saved Balls Launched: " + ballsLaunched + ", Score: " + scoreManager.getScore() + ", Success: " + success);
    }

    // Loads the saved game state from SharedPreferences
    public void loadGameState() {
        ballsLaunched = sharedPreferences.getInt(BALLS_LAUNCHED_KEY, 0);
        int score = sharedPreferences.getInt(SCORE_KEY, 0);
        scoreManager.loadScore(score);
        loadAllBalls();
        Log.d(TAG, "Loaded Balls Launched: " + ballsLaunched + ", Score: " + score);
    }

    // Saves all balls' data to SharedPreferences
    private void saveAllBalls(SharedPreferences.Editor editor) {
        editor.putInt(BALL_COUNT_KEY, ballManager.getBalls().size());
        for (int i = 0; i < ballManager.getBalls().size(); i++) {
            Ball ball = ballManager.getBalls().get(i);
            editor.putString("ball_" + i, ball.toString());
        }
    }

    // Loads all balls' data from SharedPreferences
    private void loadAllBalls() {
        ballManager.clearBalls();
        int ballCount = sharedPreferences.getInt(BALL_COUNT_KEY, 0);
        for (int i = 0; i < ballCount; i++) {
            String ballData = sharedPreferences.getString("ball_" + i, null);
            if (ballData != null) {
                Ball ball = Ball.fromString(ballData);
                if (ball != null) {
                    ballManager.addBall(ball);
                }
            }
        }
        Log.d(TAG, "Loaded " + ballCount + " balls from SharedPreferences");
    }

    // Called when the view is detached from the window, saving the game state
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        saveGameState();
    }

    // Handles accelerometer sensor events to apply acceleration to balls
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            double ax = -event.values[0] * 1.5;
            double ay = event.values[1] * 1.5;
            ballManager.applyAcceleration(ax, ay);
            postInvalidateOnAnimation();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // Adds multiple balls to the game
    public void addMultipleBalls(int count) {
        for (int i = 0; i < count; i++) {
            float x = (float) (Math.random() * getWidth());
            float y = (float) (Math.random() * getHeight());
            ballManager.addBall(0xFF00FF00, x, y, 0, 0);
        }
        ballsLaunched += count;
        saveGameState();
        postInvalidateOnAnimation();
    }

    // Clears all balls and resets the score
    public void clearBalls() {
        ballManager.clearBalls();
        scoreManager.resetScore();
        ballsLaunched = 0;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(BALL_COUNT_KEY);
        for (String key : sharedPreferences.getAll().keySet()) {
            if (key.startsWith("ball_")) {
                editor.remove(key);
            }
        }
        editor.putInt(BALLS_LAUNCHED_KEY, ballsLaunched);
        editor.putInt(SCORE_KEY, scoreManager.getScore());
        editor.apply();

        if (scoreChangeListener != null) {
            scoreChangeListener.onScoreChange(scoreManager.getScore());
        }

        Log.d(TAG, "Cleared all balls and reset score and balls launched.");
        postInvalidateOnAnimation();
    }

    // Sets the listener for score changes
    public void setOnScoreChangeListener(OnScoreChangeListener listener) {
        this.scoreChangeListener = listener;
    }

    // Interface for listening to score changes
    public interface OnScoreChangeListener {
        void onScoreChange(int score);
    }
}