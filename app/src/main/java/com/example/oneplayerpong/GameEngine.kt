package com.example.oneplayerpong

import kotlin.random.Random

class GameEngine {
    enum class GameState {
        Playing,
        GameOver
    }

    var leftPaddleY = 0f
    var rightPaddleY = 0f
    var ballX = 0f
    var ballY = 0f
    var ballRadius = 24f
    var ballVelocityX = 0f
    var ballVelocityY = 0f
    var score = 0
    var bestScore = 0
    var currentHeight = 0
    var gameState = GameState.Playing
    var cameraY = 0f
    
    var lastSpeedIncreaseScore = 0
    var speedIncreaseFlag = false
    
    // Background stars
    data class Star(val x: Float, val y: Float, val size: Float)
    val stars = mutableListOf<Star>()

    fun resetGameState(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        leftPaddleY = height * 0.4f
        rightPaddleY = height * 0.4f
        cameraY = 0f

        // Initialize stars if empty
        if (stars.isEmpty()) {
            for (i in 0 until 100) {
                stars.add(Star(
                    Random.nextFloat() * width,
                    Random.nextFloat() * height * 5 - height * 4,
                    2f + Random.nextFloat() * 5f
                ))
            }
        }

        // Randomize ball starting position
        ballX = width * 0.5f
        ballY = height * 0.6f
        
        ballVelocityX = if (Random.nextBoolean()) 420f else -420f
        ballVelocityY = -560f

        currentHeight = 0
        score = 0
        lastSpeedIncreaseScore = 0
        speedIncreaseFlag = false
        gameState = GameState.Playing
    }

    fun updateGame(deltaTime: Float, width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        if (gameState == GameState.GameOver) return

        ballX += ballVelocityX * deltaTime
        ballY += ballVelocityY * deltaTime

        // Side wall Game Over (only if ball goes fully off screen)
        if (ballX < 0f || ballX > width.toFloat()) {
            gameState = GameState.GameOver
            ballVelocityX = 0f
            ballVelocityY = 0f
            return
        }

        // Camera following (Scroll up)
        val scrollThreshold = cameraY + height * 0.4f
        if (ballY < scrollThreshold) {
            val diff = scrollThreshold - ballY
            cameraY -= diff
            
            // Repopulate stars above as we climb
            if (stars.any { it.y > cameraY + height }) {
                stars.removeIf { it.y > cameraY + height }
                while (stars.size < 100) {
                    stars.add(Star(
                        Random.nextFloat() * width,
                        cameraY - Random.nextFloat() * height,
                        2f + Random.nextFloat() * 5f
                    ))
                }
            }
        }

        // Bottom collision (Game Over relative to camera)
        if (ballY - ballRadius > cameraY + height.toFloat()) {
            gameState = GameState.GameOver
            ballVelocityX = 0f
            ballVelocityY = 0f
            return
        }

        val paddleWidth = width * 0.04f
        val paddleHeight = height * 0.18f
        
        // World coordinates for paddles
        val worldLeftPaddleY = leftPaddleY + cameraY
        val worldRightPaddleY = rightPaddleY + cameraY
        
        // Define paddle horizontal bounds to match drawing
        val leftPaddleRightX = paddleWidth * 1.5f
        val rightPaddleLeftX = width - paddleWidth * 1.5f

        // Left paddle collision (inner side)
        if (ballVelocityX < 0 && 
            ballX - ballRadius < leftPaddleRightX && 
            ballX - ballRadius > paddleWidth * 0.5f &&
            ballY in worldLeftPaddleY..(worldLeftPaddleY + paddleHeight)) {
            
            val relativeY = (ballY - worldLeftPaddleY) / paddleHeight
            val speed = Math.sqrt((ballVelocityX * ballVelocityX + ballVelocityY * ballVelocityY).toDouble()).toFloat() * 1.05f
            
            val angleDeg = if (relativeY > 0.5f) {
                Random.nextDouble(45.0, 90.0) // Lower half: 45-90 (Downwards, safe for width)
            } else {
                Random.nextDouble(91.0, 160.0) // Top half: 91-160 (Upwards)
            }
            
            val angleRad = Math.toRadians(angleDeg)
            ballVelocityX = (speed * Math.sin(angleRad)).toFloat()
            ballVelocityY = (speed * Math.cos(angleRad)).toFloat()
            ballX = leftPaddleRightX + ballRadius
        }

        // Right paddle collision (inner side)
        if (ballVelocityX > 0 && 
            ballX + ballRadius > rightPaddleLeftX && 
            ballX + ballRadius < width - paddleWidth * 0.5f &&
            ballY in worldRightPaddleY..(worldRightPaddleY + paddleHeight)) {
            
            val relativeY = (ballY - worldRightPaddleY) / paddleHeight
            val speed = Math.sqrt((ballVelocityX * ballVelocityX + ballVelocityY * ballVelocityY).toDouble()).toFloat() * 1.05f
            
            val angleDeg = if (relativeY > 0.5f) {
                Random.nextDouble(45.0, 90.0) // Lower half: 45-90 (Downwards, safe for width)
            } else {
                Random.nextDouble(91.0, 160.0) // Top half: 91-160 (Upwards)
            }
            
            val angleRad = Math.toRadians(angleDeg)
            ballVelocityX = (-speed * Math.sin(angleRad)).toFloat()
            ballVelocityY = (speed * Math.cos(angleRad)).toFloat()
            ballX = rightPaddleLeftX - ballRadius
        }

        // Scoring
        val heightScore = ((-ballY + height) / 8).toInt().coerceAtLeast(0)
        if (heightScore > currentHeight) {
            currentHeight = heightScore
            score = currentHeight
            if (score > bestScore) bestScore = score
            
            // Difficulty scaling every 1000 points
            if (score >= lastSpeedIncreaseScore + 1000) {
                lastSpeedIncreaseScore = (score / 1000) * 1000
                ballVelocityX *= 1.15f
                ballVelocityY *= 1.15f
                speedIncreaseFlag = true
            }
        }
    }
}
