package com.mika10095.escapefromkotlin.engine

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.mika10095.escapefromkotlin.input.GyroInput
import com.mika10095.escapefromkotlin.input.InputSystem
import com.mika10095.escapefromkotlin.input.TiltInput

class GameInstance(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    var gameThread: GameThread? = null
    val gameState: GameState = GameState(context)
    lateinit var inputSystem: InputSystem
    lateinit var gyroInput: GyroInput
    lateinit var tiltInput: TiltInput

    init {
        gameState.init()
        holder.addCallback(this)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        inputSystem = InputSystem(width, height)
        gyroInput = GyroInput(context)
        tiltInput = TiltInput(context)
        gyroInput.start()
        tiltInput.start()
        gameState.player.setPosition(width/2f+32,height/2f+32)
        gameState.gameMap.setPosition(width/2f-4*64,height/2f-4*64)
        gameThread = GameThread(holder,this)
        gameThread?.running = true
        gameThread?.start()
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        gyroInput.stop()
        tiltInput.stop()
        gameThread?.running = false
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawColor(Color.BLACK)
        gameState.drawState(canvas)
        drawDebug(canvas)
        update()
    }
    fun drawDebug(canvas: Canvas){
        val paint = Paint()
        /*paint.color = Color.YELLOW
        canvas.drawRect(inputSystem.forwardButton,paint)
        paint.color = Color.BLUE
        canvas.drawRect(inputSystem.backButton,paint)
        paint.color = Color.CYAN
        canvas.drawRect(inputSystem.shootButton,paint)*/
        paint.color = Color.MAGENTA
        paint.textSize = 40f
        canvas.drawText(
            "Movement: ${inputSystem.movementInput}",
            20f,
            250f,
            paint
        )
        canvas.drawText(
            "Gyro: ${gyroInput.yaw}",
            20f,
            150f,
            paint
        )
        canvas.drawText(
            "Turn: ${tiltInput.turn} Tilt: ${tiltInput.tilt}",
            20f,
            200f,
            paint
        )
        canvas.drawText("FPS: " + gameThread?.FPS.toString()+"\tUpdateMS: "+ gameThread?.updateMili.toString()+"\t DT: " + gameThread?.dt.toString(),
            20f,
            100f,
            paint)
    }
    fun update()
    {
        gameThread?.let {
            inputSystem.setSensorInput(gyroInput,tiltInput)
            gameState.updateState(it.dt, inputSystem)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_MOVE -> {
                // Reset inputs for this frame
                var forward = false
                var back = false
                var shoot = false

                for (i in 0 until event.pointerCount) {
                    val x = event.getX(i)
                    val y = event.getY(i)

                    if (inputSystem.forwardButton.contains(x, y)) forward = true
                    if (inputSystem.backButton.contains(x, y)) back = true
                    if (inputSystem.shootButton.contains(x, y)) shoot = true
                }

                // Apply inputs
                inputSystem.movementInput = when {
                    forward -> 1f
                    back -> -1f
                    else -> 0f
                }
                inputSystem.shootInput = shoot
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                // Reset all inputs when any finger lifts; you could refine this to track pointers individually
                inputSystem.movementInput = 0f
                inputSystem.shootInput = false
            }
        }

        return true
    }
}