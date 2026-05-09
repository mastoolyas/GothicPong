package com.example.oneplayerpong

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Load existing high score into engine
        val prefs = getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        val highScore = prefs.getInt("high_score", 0)
        
        gameView = GameView(this)
        gameView.engine.bestScore = highScore
        
        setContentView(gameView)
    }

    override fun onPause() {
        super.onPause()
        saveHighScore()
    }

    private fun saveHighScore() {
        val prefs = getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        val currentHigh = prefs.getInt("high_score", 0)
        if (gameView.engine.bestScore > currentHigh) {
            prefs.edit().putInt("high_score", gameView.engine.bestScore).apply()
        }
    }
}
