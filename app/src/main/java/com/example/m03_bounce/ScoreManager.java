package com.example.m03_bounce;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ScoreManager {
    private int score;
    private SharedPreferences sharedPreferences;  // SharedPreferences for storing the score persistently
    private static final String SCORE_KEY = "score";
    private static final String TAG = "ScoreManager";

    // Constructor initializes SharedPreferences and loads the saved score
    public ScoreManager(Context context) {
        sharedPreferences = context.getSharedPreferences("BouncingBallPrefs", Context.MODE_PRIVATE);
        loadScore(score);
    }

    // Increments the score by 1 and saves the updated score
    public void incrementScore() {
        score++;
        saveScore();
    }

    // Returns the current score
    public int getScore() {
        return score;
    }

    // Saves the current score to SharedPreferences
    public void saveScore() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SCORE_KEY, score);
        boolean success = editor.commit();
        Log.d(TAG, "Score saved successfully: " + success);
    }

    // Loads the saved score from SharedPreferences
    public void loadScore(int score) {
        this.score = sharedPreferences.getInt(SCORE_KEY, 0);
        Log.d(TAG, "Loaded score: " + this.score);
    }

    // Resets the score to 0 and saves it
    public void resetScore() {
        score = 0;
        saveScore();
    }
}
