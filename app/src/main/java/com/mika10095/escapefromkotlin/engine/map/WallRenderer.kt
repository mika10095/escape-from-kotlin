package com.mika10095.escapefromkotlin.engine.map

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import com.mika10095.escapefromkotlin.R
import com.mika10095.escapefromkotlin.engine.GameState
import com.mika10095.escapefromkotlin.engine.raycast.RayCaster
import kotlin.math.cos
import kotlin.math.sin
import androidx.core.graphics.get

class WallRenderer(context: Context) {
    val raycaster = RayCaster()
    val wallTextures = arrayOf(BitmapFactory.decodeResource(context.resources, R.drawable.wall_brick))

    fun draw(state: GameState, canvas: Canvas) {

        val player = state.player
        val map = state.gameMap

        val paint = Paint()

        val rays = 128
        val fov = Math.toRadians(60.0).toFloat()

        val screenW = canvas.width.toFloat()
        val screenH = canvas.height.toFloat()

        val columnWidth = screenW / rays

        for (i in 0 until rays) {

            val rayAngle =
                player.rot - fov/2 +
                        (i + 0.5f) / rays * fov

            val hit = raycaster.castRay(
                player.posx,
                player.posy,
                rayAngle,
                map
            )

            val corrected = hit.distance * cos(rayAngle - player.rot)
            //println(hit)
            val wallHeight =
                (screenH * 64f) / corrected

            val x = i * columnWidth

            val top = screenH/2 - wallHeight/2
            val bottom = screenH/2 + wallHeight/2

            val tex = wallTextures[0]

            var texX = (hit.wallX * tex.width).toInt().coerceIn(0, tex.width - 1)

            val dirX = cos(rayAngle)
            val dirY = sin(rayAngle)

            if (hit.side == 0 && dirX > 0) texX = tex.width - texX - 1
            if (hit.side == 1 && dirY < 0) texX = tex.width - texX - 1

            val srcX = texX

            val srcRect = android.graphics.Rect(
                srcX,
                0,
                srcX + 1,
                tex.height
            )

            val dstRect = android.graphics.RectF(
                x,
                top,
                x + columnWidth,
                bottom
            )

            canvas.drawBitmap(tex, srcRect, dstRect, null)
        }
    }
}