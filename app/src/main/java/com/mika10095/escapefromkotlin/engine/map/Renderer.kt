package com.mika10095.escapefromkotlin.engine.map

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import com.mika10095.escapefromkotlin.R
import com.mika10095.escapefromkotlin.engine.GameState
import com.mika10095.escapefromkotlin.engine.raycast.RayCaster
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Renderer(context: Context) {
    val raycaster = RayCaster()
    val srcRect = Rect()
    val dstRect = RectF()


    val wallTextures = arrayOf(
        BitmapFactory.decodeResource(context.resources, R.drawable.wall_brick),
        BitmapFactory.decodeResource(context.resources, R.drawable.wall_brick),
        BitmapFactory.decodeResource(context.resources, R.drawable.wall_brick),
        BitmapFactory.decodeResource(context.resources, R.drawable.wall_brick),
        BitmapFactory.decodeResource(context.resources, R.drawable.door_default)

    )
    val enemyTextures = arrayOf(
        BitmapFactory.decodeResource(context.resources, R.drawable.enemy_1),
        BitmapFactory.decodeResource(context.resources, R.drawable.enemy_2),
        BitmapFactory.decodeResource(context.resources, R.drawable.enemy_3),
        BitmapFactory.decodeResource(context.resources, R.drawable.enemy_4),
        BitmapFactory.decodeResource(context.resources, R.drawable.enemy_shoot)

    )
    val weaponTextures = arrayOf(
        BitmapFactory.decodeResource(context.resources, R.drawable.pistol_shoot_1),
        BitmapFactory.decodeResource(context.resources, R.drawable.pistol_shoot_2),
    )


    fun draw(state: GameState, canvas: Canvas) {

        val player = state.player
        val map = state.gameMap

        val paint = Paint()

        val rays = state.settingsManager.rayCount
        val fov = Math.toRadians(state.settingsManager.fov.toDouble()).toFloat()

        val screenW = canvas.width.toFloat()
        val screenH = canvas.height.toFloat()
        val columnWidth = screenW / rays
        val columnBitmap = createBitmap(rays, canvas.height)
        val columnCanvas = Canvas(columnBitmap)

        for (i in 0 until rays) {

            val rayAngle =
                player.rot - fov / 2 +
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

            val top = screenH / 2 - wallHeight / 2
            val bottom = screenH / 2 + wallHeight / 2

            val tex = wallTextures[hit.tile]


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
            val dstRect = RectF(i.toFloat(), top, i + 1f, bottom) // width=1 pixel in bitmap
            columnCanvas.drawBitmap(tex, srcRect, dstRect, paint)
        }
        val dst = RectF(0f, 0f, screenW, screenH)
        canvas.drawBitmap(columnBitmap, null, dst, null)
    }

    fun drawEnemies(state: GameState, canvas: Canvas) {

        val player = state.player
        val enemies = state.enemies

        val screenW = canvas.width.toFloat()
        val screenH = canvas.height.toFloat()

        val fov = Math.toRadians(state.settingsManager.fov.toDouble()).toFloat()

        for (enemy in enemies) {
            if (!enemy.visible) continue

            val dx = enemy.posx - player.posx
            val dy = enemy.posy - player.posy

            val distance = sqrt(dx * dx + dy * dy)

            val angleToEnemy = atan2(dy, dx)

            val angleDiff = angleToEnemy - player.rot

            if (abs(angleDiff) > fov / 2) continue

            var diff = angleToEnemy - enemy.rot
            while (diff > Math.PI) diff -= (2 * Math.PI).toFloat()
            while (diff < -Math.PI) diff += (2 * Math.PI).toFloat()
            val spriteAngle = Math.toDegrees(diff.toDouble()).toFloat()

            val sprite = when {
                enemy.shooting -> 4
                spriteAngle > -45 && spriteAngle <= 45 -> 2     // front
                spriteAngle > 45 && spriteAngle <= 135 -> 3     // left
                spriteAngle <= -45 && spriteAngle > -135 -> 1   // right
                else -> 0                      // back
            }
            val screenX =
                (0.5f + angleDiff / fov) * screenW

            val size =
                (screenH * 64f) / distance

            val top = screenH / 2 - size / 2
            val bottom = screenH / 2 + size / 2

            val left = screenX - size / 2
            val right = screenX + size / 2f

            val dst = RectF(left, top, right, bottom)

            canvas.drawBitmap(enemyTextures[enemy.spriteId * 5 + sprite], null, dst, null)
        }
    }

    fun drawWeapon(state: GameState, canvas: Canvas) {
        val weaponWidth = 64f * 16
        val weaponHeight = 64f * 16

        val left = (canvas.width - weaponWidth) / 2f   // center horizontally
        val top = canvas.height - weaponHeight         // bottom of screen
        val right = left + weaponWidth
        val bottom = canvas.height.toFloat()

        RectF(left, top, right, bottom)
        var spriteId = 0
        if (state.player.shooting)
            spriteId++
        val baseBitmap = weaponTextures[spriteId]
        val scaledBitmap = baseBitmap.scale(weaponWidth.toInt(), weaponHeight.toInt(), true)
        val paint = Paint()
        paint.isFilterBitmap = false
        canvas.drawBitmap(scaledBitmap, left, top, paint)
    }
}