package com.mika10095.escapefromkotlin

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.mika10095.escapefromkotlin.Entities.Enemy
import com.mika10095.escapefromkotlin.Entities.Player
import kotlin.math.cos
import kotlin.math.sin

class GameState {
    val player = Player()
    val enemies = mutableListOf<Enemy>()

    fun drawState(canvas: Canvas)
    {
        drawPlayer(canvas)
    }
    fun drawPlayer(canvas: Canvas)
    {
        val paint = Paint()
        paint.color = Color.RED
        canvas.drawCircle(player.posx,player.posy,player.radius,paint)
        paint.color = Color.GREEN
        paint.strokeWidth = 5f
        canvas.drawLine(player.posx,player.posy,player.posx+40*cos(player.rot),player.posy+40*sin(player.rot),paint)
    }
    fun updateState(dt: Double, inputSystem : InputSystem)
    {
        player.posx += inputSystem.movementInput*cos(player.rot)*dt.toFloat()*player.speed
        player.posy += inputSystem.movementInput*sin(player.rot)*dt.toFloat()*player.speed
        player.rot += inputSystem.turnInputGravity * dt.toFloat() * 2f
        player.rot += inputSystem.turnInputGyro * dt.toFloat() * 2f
        //player.rot += 1*dt.toFloat()
    }
}