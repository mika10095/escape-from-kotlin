package com.mika10095.escapefromkotlin.engine.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.mika10095.escapefromkotlin.R
import com.mika10095.escapefromkotlin.engine.GameState
import com.mika10095.escapefromkotlin.engine.raycast.RayCaster
import kotlin.math.cos
import kotlin.math.sin
import androidx.core.graphics.get
import kotlin.math.sqrt
import androidx.core.graphics.createBitmap

class WallRenderer(context: Context) {
    val raycaster = RayCaster()
    val srcRect = android.graphics.Rect()
    val dstRect = android.graphics.RectF()


    val wallTextures = arrayOf(
        BitmapFactory.decodeResource(context.resources, R.drawable.wall_brick)
    )

    fun draw(state: GameState, canvas: Canvas) {

        val player = state.player
        val map = state.gameMap

        val paint = Paint()

        val rays = 128
        val fov = Math.toRadians(72.0).toFloat()

        val screenW = canvas.width.toFloat()
        val screenH = canvas.height.toFloat()

        val columnWidth = screenW / rays
        val columnBitmap = createBitmap(rays, canvas.height)
        val columnCanvas = Canvas(columnBitmap)

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

            srcRect.left = srcX
            srcRect.top = 0
            srcRect.right = srcX + 1
            srcRect.bottom = tex.height

            dstRect.left = x
            dstRect.top = top
            dstRect.right = x + columnWidth
            dstRect.bottom = bottom

            val shade = ((1f - corrected / 512f) * 255f).toInt().coerceIn(0, 255)
            paint.colorFilter = LightingColorFilter(Color.rgb(shade, shade, shade), 0)
            paint.isFilterBitmap = false

            val srcRect = Rect(texX, 0, texX + 1, tex.height)
            val dstRect = RectF(i.toFloat(), top, i+1f, bottom) // width=1 pixel in bitmap
            columnCanvas.drawBitmap(tex, srcRect, dstRect, paint)
        }
        val dst = RectF(0f, 0f, screenW, screenH)
        canvas.drawBitmap(columnBitmap, null, dst, null)
    }
}