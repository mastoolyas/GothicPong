package com.example.oneplayerpong

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, Runnable {
    private val paint = Paint().apply {
        isAntiAlias = true
        textSize = 64f
        color = Color.WHITE
    }
    private var gameThread: Thread? = null
    @Volatile
    private var running = false

    private val engine = GameEngine()
    private var lastFrameTime = 0L
    private var notificationTimer = 0f

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        engine.resetGameState(width, height)
        running = true
        gameThread = Thread(this).also { it.start() }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = Unit

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        running = false
        gameThread?.join()
    }

    override fun run() {
        lastFrameTime = System.currentTimeMillis()
        while (running) {
            val now = System.currentTimeMillis()
            val deltaTime = ((now - lastFrameTime).coerceAtLeast(1)) / 1000f
            lastFrameTime = now
            engine.updateGame(deltaTime, width, height)
            
            if (engine.speedIncreaseFlag) {
                engine.speedIncreaseFlag = false
                notificationTimer = 2.0f // Show for 2 seconds
            }
            if (notificationTimer > 0) {
                notificationTimer -= deltaTime
            }
            
            drawFrame()
            try {
                Thread.sleep(16)
            } catch (e: InterruptedException) {
                // Ignore
            }
        }
    }

    private fun drawFrame() {
        val canvas: Canvas = holder.lockCanvas() ?: return
        try {
            // Dark space background
            canvas.drawColor(Color.rgb(10, 15, 30))

            // Draw background stars
            paint.color = Color.WHITE
            engine.stars.forEach { star ->
                val screenY = star.y - engine.cameraY
                if (screenY in 0f..height.toFloat()) {
                    canvas.drawCircle(star.x, screenY, star.size, paint)
                }
            }

            // Draw height markers
            paint.color = Color.argb(50, 255, 255, 255)
            paint.textSize = 30f
            val firstLine = ((engine.cameraY / 500).toInt() * 500)
            for (y in firstLine..(firstLine + height.toInt() + 500) step 500) {
                val screenY = y - engine.cameraY
                canvas.drawLine(0f, screenY, width.toFloat(), screenY, paint)
                canvas.drawText("${(height - y) / 8}m", 20f, screenY - 10f, paint)
            }

            paint.color = Color.WHITE
            paint.textSize = 64f
            canvas.drawText("Height: ${engine.score}", 40f, 90f, paint)
            canvas.drawText("Best: ${engine.bestScore}", 40f, 170f, paint)

            val paddleWidth = width * 0.04f
            val paddleHeight = height * 0.18f
            paint.color = Color.CYAN
            canvas.drawRect(paddleWidth * 0.5f, engine.leftPaddleY, paddleWidth * 1.5f, engine.leftPaddleY + paddleHeight, paint)
            canvas.drawRect(width - paddleWidth * 1.5f, engine.rightPaddleY, width - paddleWidth * 0.5f, engine.rightPaddleY + paddleHeight, paint)

            paint.color = Color.YELLOW
            canvas.drawCircle(engine.ballX, engine.ballY - engine.cameraY, engine.ballRadius, paint)

            paint.color = Color.LTGRAY
            paint.textSize = 36f
            canvas.drawText("Hold left + right sides to move both paddles", 40f, height - 100f, paint)
            canvas.drawText("Keep the ball up and climb higher", 40f, height - 50f, paint)

            if (notificationTimer > 0) {
                paint.color = Color.YELLOW
                paint.textSize = 80f
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("SPEED UP!", width / 2f, height / 3f, paint)
                paint.textAlign = Paint.Align.LEFT
            }

            if (engine.gameState == GameEngine.GameState.GameOver) {
                paint.color = Color.argb(220, 0, 0, 0)
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

                paint.color = Color.WHITE
                paint.textSize = 88f
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("Game Over", width / 2f, height / 2f - 40f, paint)

                paint.textSize = 48f
                canvas.drawText("Tap anywhere to restart", width / 2f, height / 2f + 50f, paint)
                paint.textAlign = Paint.Align.LEFT
            }
        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (engine.gameState == GameEngine.GameState.GameOver && event.action == MotionEvent.ACTION_DOWN) {
            engine.resetGameState(width, height)
            return true
        }

        for (pointerIndex in 0 until event.pointerCount) {
            val x = event.getX(pointerIndex)
            val y = event.getY(pointerIndex)
            val targetY = (y - height * 0.09f).coerceIn(0f, height - height * 0.18f)
            if (x < width / 2f) {
                engine.leftPaddleY = targetY
            } else {
                engine.rightPaddleY = targetY
            }
        }
        return true
    }
}
