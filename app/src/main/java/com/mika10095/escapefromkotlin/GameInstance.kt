package com.mika10095.escapefromkotlin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class GameInstance(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    var gameThread: GameThread? = null
    val gameState: GameState = GameState()
    lateinit var inputSystem: InputSystem
    lateinit var gyroInput: GyroInput
    lateinit var tiltInput: TiltInput

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        inputSystem = InputSystem(width,height)
        gyroInput = GyroInput(context)
        tiltInput = TiltInput(context)
        gyroInput.start()
        tiltInput.start()
        gameState.player.setPosition(width/2f,height/2f)
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
        drawDebug(canvas)
        gameState.drawState(canvas)
        val paint = Paint()
        update()
    }
    fun drawDebug(canvas: Canvas){
        val paint = Paint()
        paint.color = Color.YELLOW
        canvas.drawRect(inputSystem.forwardButton,paint)
        paint.color = Color.BLUE
        canvas.drawRect(inputSystem.backButton,paint)
        paint.color = Color.CYAN
        canvas.drawRect(inputSystem.shootButton,paint)
        paint.color = Color.MAGENTA
        paint.textSize = 40f
        canvas.drawText(
            "Gyro: ${gyroInput.yaw}",
            20f,
            150f,
            paint
        )
        canvas.drawText(
            "Turn: ${tiltInput.turn}",
            20f,
            200f,
            paint
        )
        canvas.drawText("FPS: " + gameThread?.FPS.toString()+"\tUpdateMS: "+ gameThread?.updateMili.toString()+"\t DT: " + gameThread?.dt.toString(),20f,100f,paint)
    }
    fun update()
    {
        val turnSpeed = gyroInput.yaw
        val turnSpeedRotate = tiltInput.turn
        gameState.player.posx += inputSystem.movementInput*cos(gameState.player.rot)
        gameState.player.posy += inputSystem.movementInput*sin(gameState.player.rot)
        gameThread?.let { gameState.player.rot += turnSpeedRotate * it.dt.toFloat() * 2f }
        gameThread?.let { gameState.player.rot += turnSpeed * it.dt.toFloat() * 2f }
        gameThread?.let { gameState.updateState(it.dt) }



    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                for(i in 0 until event.pointerCount){

                    val x = event.getX(i)
                    val y = event.getY(i)

                    if(inputSystem.forwardButton.contains(x,y))
                        inputSystem.movementInput = 1f

                    if(inputSystem.backButton.contains(x,y))
                        inputSystem.movementInput = -1f

                    if(inputSystem.shootButton.contains(x,y))
                        inputSystem.shootInput = true
                }
            return true
            }
            MotionEvent.ACTION_MOVE -> {
                for(i in 0 until event.pointerCount){

                    val x = event.getX(i)
                    val y = event.getY(i)

                    if(inputSystem.forwardButton.contains(x,y))
                        inputSystem.movementInput = 1f

                    if(inputSystem.backButton.contains(x,y))
                        inputSystem.movementInput = -1f

                    if(inputSystem.shootButton.contains(x,y))
                        inputSystem.shootInput = true
                }
                return true
            }
            MotionEvent.ACTION_UP ->{
                inputSystem.shootInput = false
                inputSystem.movementInput = 0f
                return true
            }
        }


        return super.onTouchEvent(event)
    }
}