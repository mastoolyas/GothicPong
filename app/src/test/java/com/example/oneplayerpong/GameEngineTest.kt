package com.example.oneplayerpong

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameEngineTest {
    private lateinit var engine: GameEngine
    private val width = 1000
    private val height = 2000

    @Before
    fun setUp() {
        engine = GameEngine()
        engine.resetGameState(width, height)
    }

    @Test
    fun testInitialState() {
        assertTrue(engine.ballX in (width * 0.3f)..(width * 0.7f))
        assertEquals(height * 0.6f, engine.ballY, 0.1f)
        assertEquals(GameEngine.GameState.Playing, engine.gameState)
        assertEquals(0, engine.score)
    }

    @Test
    fun testClimbingAndCamera() {
        // ballY starts at 1200 (0.6 * 2000). Threshold is 800.
        engine.ballY = 700f 
        engine.updateGame(0.01f, width, height)
        
        // Camera should have scrolled
        assertTrue("Camera should have scrolled up (negative Y)", engine.cameraY < 0)
        assertEquals(engine.ballY - height * 0.4f, engine.cameraY, 0.1f)
    }

    @Test
    fun testBottomGameOverWithCamera() {
        // Move camera and ball up
        engine.cameraY = -5000f
        // Set ball below visible area and ensure it stays there or moves further down
        engine.ballY = engine.cameraY + height + 50f 
        engine.ballVelocityY = 100f
        
        engine.updateGame(0.1f, width, height)
        assertEquals(GameEngine.GameState.GameOver, engine.gameState)
    }

    @Test
    fun testNoTopWallBounce() {
        engine.ballY = -1000f // Way above initial screen
        engine.ballVelocityY = -100f // Still moving up
        
        val prevY = engine.ballY
        engine.updateGame(0.1f, width, height)
        
        // Should continue moving up, not bounce
        assertTrue("Ball should continue moving up", engine.ballY < prevY)
        assertTrue("Velocity should remain negative", engine.ballVelocityY < 0)
    }

    @Test
    fun testScoringClimb() {
        engine.ballY = -8000f // Climbed 10000 pixels (since height is 2000)
        engine.updateGame(0.01f, width, height)
        
        val expectedScore = ((-(-8000f) + 2000f) / 8).toInt()
        assertEquals(expectedScore, engine.score)
    }

    @Test
    fun testDifficultyScaling() {
        val initialVelX = engine.ballVelocityX
        val initialVelY = engine.ballVelocityY
        
        // Move ball to get 1100 points
        // score = (-ballY + 2000) / 8
        // 1100 * 8 = 8800
        // -ballY + 2000 = 8800 => ballY = -6800
        engine.ballY = -6800f
        engine.updateGame(0.01f, width, height)
        
        assertTrue("Score should be over 1000", engine.score >= 1000)
        assertTrue("Velocity X should increase", Math.abs(engine.ballVelocityX) > Math.abs(initialVelX))
        assertTrue("Velocity Y should increase", Math.abs(engine.ballVelocityY) > Math.abs(initialVelY))
        assertTrue("Speed increase flag should be set", engine.speedIncreaseFlag)
    }
}
