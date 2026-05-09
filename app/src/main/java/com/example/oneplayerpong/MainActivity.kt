package com.example.oneplayerpong

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.games.PlayGamesSdk
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.AuthenticationResult
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity() {

    private lateinit var highScoreText: TextView
    private lateinit var userNameText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Play Games SDK
        try {
            PlayGamesSdk.initialize(this)
        } catch (e: Exception) {
            // SDK initialization might fail if not configured correctly in console
        }
        
        setContentView(R.layout.activity_main)

        highScoreText = findViewById(R.id.highScoreValue)
        userNameText = findViewById(R.id.userNameText)

        findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        findViewById<Button>(R.id.btnQuit).setOnClickListener {
            finishAffinity()
        }

        findViewById<Button>(R.id.btnSignIn).setOnClickListener {
            signIn()
        }

        updateHighScore()
        
        // Try to auto sign-in
        checkAuthentication()
    }

    override fun onResume() {
        super.onResume()
        updateHighScore()
    }

    private fun updateHighScore() {
        val prefs = getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        val high = prefs.getInt("high_score", 0)
        highScoreText.text = high.toString()
    }

    private fun checkAuthentication() {
        PlayGames.getGamesSignInClient(this).isAuthenticated.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result.isAuthenticated) {
                updatePlayerInfo()
            }
        }
    }

    private fun signIn() {
        PlayGames.getGamesSignInClient(this).signIn().addOnCompleteListener { task ->
            if (task.isSuccessful && task.result.isAuthenticated) {
                updatePlayerInfo()
            }
        }
    }

    private fun updatePlayerInfo() {
        PlayGames.getPlayersClient(this).currentPlayer.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userNameText.text = task.result.displayName
            }
        }
    }
}
