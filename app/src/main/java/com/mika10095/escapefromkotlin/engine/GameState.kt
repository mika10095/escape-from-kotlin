package com.mika10095.escapefromkotlin.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.mika10095.escapefromkotlin.engine.map.GameMap
import com.mika10095.escapefromkotlin.engine.map.WallRenderer
import com.mika10095.escapefromkotlin.ents.Enemy
import com.mika10095.escapefromkotlin.ents.Player
import com.mika10095.escapefromkotlin.input.InputSystem
import kotlin.math.cos
import kotlin.math.sin

class GameState(context: Context) {
    val wallRenderer = WallRenderer(context)
    val player = Player()
    val enemies = mutableListOf<Enemy>()
    val gameMap = GameMap(8,8,arrayOf(
        1,1,1,1,1,1,1,1,
        1,0,0,0,0,0,0,1,
        1,0,0,0,0,0,1,1,
        1,0,1,1,0,1,1,1,
        1,0,1,0,0,0,1,1,
        1,0,1,0,0,0,0,1,
        1,0,0,0,0,0,0,1,
        1,1,1,1,1,1,1,1,
    ))

    fun drawState(canvas: Canvas)
    {
        val paint = Paint()
        paint.color = Color.BLUE
        canvas.drawRect(0f,0f,canvas.width.toFloat(),canvas.height/2f,paint)
        paint.color = Color.GREEN
        canvas.drawRect(0f,canvas.height/2f,canvas.width.toFloat(),canvas.height.toFloat(),paint)
        wallRenderer.draw(this, canvas)
        drawMap(canvas)
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
    fun drawMap(canvas: Canvas)
    {
        val paint = Paint()
        paint.color = Color.RED
        for (y in 0..<gameMap.height)
        {
            for (x in 0..<gameMap.width)
            {
                val index = y * gameMap.width + x
                if (gameMap.map[index] == 1) {
                    paint.color = Color.RED

                    val left = gameMap.posX + x * gameMap.tileSize
                    val top = gameMap.posY + y * gameMap.tileSize
                    val right = left + gameMap.tileSize
                    val bottom = top + gameMap.tileSize

                    canvas.drawRect(left, top, right, bottom, paint)
                }
            }
        }
    }
    fun updateState(dt: Double, inputSystem : InputSystem)
    {
        val moveX = cos(player.rot) * inputSystem.movementInput * dt.toFloat() * player.speed
        val moveY = sin(player.rot) * inputSystem.movementInput * dt.toFloat() * player.speed
        if (!gameMap.isWallCircle(player.posx + moveX, player.posy, player.radius)) {
            player.posx += moveX
        }
        if (!gameMap.isWallCircle(player.posx, player.posy + moveY, player.radius)) {
            player.posy += moveY
        }
        player.rot += inputSystem.turnInputGravity * dt.toFloat() * player.turnspeed
        player.rot -= inputSystem.turnInputGyro * dt.toFloat() * player.turnspeed/2
        if (player.rot < 0f) player.rot += (2 * Math.PI).toFloat()
        if (player.rot > (2 * Math.PI).toFloat()) player.rot -= (2 * Math.PI).toFloat()
        //player.rot += 1*dt.toFloat()
    }
}