package com.example.m03_bounce;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements BouncingBallView.OnScoreChangeListener {

    private BouncingBallView bouncingBallView;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView scoreTextView;
    private TextView ballsLaunchedTextView;
    private EditText ballCountInput;
    private Button addBallButton;
    private Button clearBallsButton;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI and view components
        bouncingBallView = findViewById(R.id.custView);
        bouncingBallView.setOnScoreChangeListener(this);
        scoreTextView = findViewById(R.id.scoreText);
        ballsLaunchedTextView = findViewById(R.id.ballsLaunchedText);
        ballCountInput = findViewById(R.id.ballCountInput);
        addBallButton = findViewById(R.id.russButton);
        clearBallsButton = findViewById(R.id.clearButton);

        // Set initial score and balls launched values
        onScoreChange(bouncingBallView.scoreManager.getScore());
        ballsLaunchedTextView.setText("Balls Launched: " + bouncingBallView.ballsLaunched);

        // Set up button listeners
        addBallButton.setOnClickListener(view -> {
            String input = ballCountInput.getText().toString();
            int count = 1;
            try {
                count = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid ball count input");
            }
            bouncingBallView.addMultipleBalls(count);
            ballsLaunchedTextView.setText("Balls Launched: " + bouncingBallView.ballsLaunched);
        });

        clearBallsButton.setOnClickListener(view -> {
            bouncingBallView.clearBalls();
            ballsLaunchedTextView.setText("Balls Launched: 0");
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Loading game state on resume.");
        bouncingBallView.loadGameState();
        onScoreChange(bouncingBallView.scoreManager.getScore());
        ballsLaunchedTextView.setText("Balls Launched: " + bouncingBallView.ballsLaunched);

        // Register the accelerometer listener
        if (accelerometer != null) {
            sensorManager.registerListener(bouncingBallView, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Saving game state on pause.");
        bouncingBallView.saveGameState();

        if (accelerometer != null) {
            sensorManager.unregisterListener(bouncingBallView);
        }
    }

    @Override
    public void onScoreChange(int score) {
        if (scoreTextView != null) {
            scoreTextView.setText("Score: " + score);
        } else
            Log.e(TAG, "scoreTextView is null when updating score!");
        }
    }

