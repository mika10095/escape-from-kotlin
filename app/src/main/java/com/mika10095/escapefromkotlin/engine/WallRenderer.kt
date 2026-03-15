package com.mika10095.escapefromkotlin.engine

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class WallRenderer {
    val raycaster = RayCaster()

    fun draw(state: GameState, canvas: Canvas) {

        val player = state.player
        val map = state.gameMap

        val paint = Paint()

        val rays = 60
        val fov = Math.toRadians(60.0).toFloat()

        val screenW = canvas.width.toFloat()
        val screenH = canvas.height.toFloat()

        val columnWidth = screenW / rays

        for (i in 0 until rays) {

            val rayAngle =
                player.rot - fov/2 +
                        i.toFloat()/rays * fov

            val dist = raycaster.castRay(
                player.posx,
                player.posy,
                rayAngle,
                map
            )

            val corrected =
                dist * kotlin.math.cos(rayAngle - player.rot)
            println(dist)
            val wallHeight =
                (screenH * 64f) / corrected

            val x = i * columnWidth

            val top = screenH/2 - wallHeight/2
            val bottom = screenH/2 + wallHeight/2

            paint.color = Color.WHITE

            canvas.drawRect(
                x,
                top,
                x + columnWidth + 1,
                bottom,
                paint
            )
        }
    }
}