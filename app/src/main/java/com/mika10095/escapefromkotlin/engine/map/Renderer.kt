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
import com.mika10095.escapefromkotlin.R
import com.mika10095.escapefromkotlin.engine.GameState
import com.mika10095.escapefromkotlin.engine.raycast.RayCaster
import com.mika10095.escapefromkotlin.ents.Enemy
import com.mika10095.escapefromkotlin.ents.PickupItem
import com.mika10095.escapefromkotlin.ents.Prop
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
        BitmapFactory.decodeResource(context.resources, R.drawable.wall_brick_secret),
        BitmapFactory.decodeResource(context.resources, R.drawable.door_default),
        BitmapFactory.decodeResource(context.resources, R.drawable.wall_brick_secret_door),
        BitmapFactory.decodeResource(context.resources, R.drawable.door_exit)

    )
    val enemyTextures = arrayOf(
        BitmapFactory.decodeResource(context.resources, R.drawable.enemy_1),
        BitmapFactory.decodeResource(context.resources, R.drawable.enemy_2),
        BitmapFactory.decodeResource(context.resources, R.drawable.enemy_3),
        BitmapFactory.decodeResource(context.resources, R.drawable.enemy_4),
        BitmapFactory.decodeResource(context.resources, R.drawable.enemy_shoot_ready),
        BitmapFactory.decodeResource(context.resources, R.drawable.enemy_shoot),
        BitmapFactory.decodeResource(context.resources, R.drawable.enemy_dead)
    )
    val propTextures = arrayOf(
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_0),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_1),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_2),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_3),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_4),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_5),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_6),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_7),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_8),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_9),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_10),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_11),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_12),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_13),
        //solid
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_14),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_15),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_16),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_17),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_18),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_19),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_20),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_21),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_22),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_23),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_24),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_25),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_26),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_27),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_28),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_29),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_30),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_31),
        BitmapFactory.decodeResource(context.resources, R.drawable.prop_32),
    )
    val pickupables = arrayOf(
        BitmapFactory.decodeResource(context.resources, R.drawable.pickup_shotgun), //-200
        BitmapFactory.decodeResource(context.resources, R.drawable.pickup_panzerfaust),//-201
        BitmapFactory.decodeResource(context.resources, R.drawable.pickup_health_small),//-202
        BitmapFactory.decodeResource(context.resources, R.drawable.pickup_health_medium),//-203
        BitmapFactory.decodeResource(context.resources, R.drawable.pickup_health_large),//-204
        BitmapFactory.decodeResource(context.resources, R.drawable.pickup_armor_small),//-205
        BitmapFactory.decodeResource(context.resources, R.drawable.pickup_armor_large),//-206
        BitmapFactory.decodeResource(context.resources, R.drawable.pickup_ammo_pistol),//-207
        BitmapFactory.decodeResource(context.resources, R.drawable.pickup_ammo_shotgun),//-208
        BitmapFactory.decodeResource(context.resources, R.drawable.pickup_treasure_small),//-209
        BitmapFactory.decodeResource(context.resources, R.drawable.pickup_treasure_medium),//-210
        BitmapFactory.decodeResource(context.resources, R.drawable.pickup_treasure_large),//-211
        BitmapFactory.decodeResource(context.resources, R.drawable.pickup_treasure_huge),//-212
        BitmapFactory.decodeResource(context.resources, R.drawable.pickup_key_1),////-213

    )
    val weaponTextures = arrayOf(
        BitmapFactory.decodeResource(context.resources, R.drawable.knife_stab_1),
        BitmapFactory.decodeResource(context.resources, R.drawable.knife_stab_2),
        BitmapFactory.decodeResource(context.resources, R.drawable.pistol_shoot_1),
        BitmapFactory.decodeResource(context.resources, R.drawable.pistol_shoot_2),
        BitmapFactory.decodeResource(context.resources, R.drawable.shotgun_shoot_1),
        BitmapFactory.decodeResource(context.resources, R.drawable.shotgun_shoot_2),
        BitmapFactory.decodeResource(context.resources, R.drawable.rocket_shoot_1),
        BitmapFactory.decodeResource(context.resources, R.drawable.rocket_shoot_2)
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

            val shade = ((1f - corrected / 1024) * 255f).toInt().coerceIn(0, 255)
            paint.colorFilter = LightingColorFilter(Color.rgb(shade, shade, shade), 0)
            paint.isFilterBitmap = false

            val srcRect = Rect(texX, 0, texX + 1, tex.height)
            val dstRect = RectF(i.toFloat(), top, i + 1f, bottom) // width=1 pixel in bitmap
            columnCanvas.drawBitmap(tex, srcRect, dstRect, paint)
        }
        val dst = RectF(0f, 0f, screenW, screenH)
        canvas.drawBitmap(columnBitmap, null, dst, null)
    }

    fun drawEntities(state: GameState, canvas: Canvas) {
        val player = state.player

        val sorted = state.entities.sortedByDescending {
            val dx = it.posx - player.posx
            val dy = it.posy - player.posy
            dx * dx + dy * dy
        }

        for (entity in sorted) {
            if (!entity.visible) continue

            val dx = entity.posx - player.posx
            val dy = entity.posy - player.posy

            val distance = sqrt(dx * dx + dy * dy)

            val angleTo = atan2(dy, dx)
            val angleDiff = entity.angleDiff(angleTo, player.rot)

            val fov = Math.toRadians(state.settingsManager.fov.toDouble()).toFloat()

            if (abs(angleDiff) > fov / 2) continue

            val screenX = (0.5f + angleDiff / fov) * canvas.width

            val size = (canvas.height * 64f) / distance

            val dst = RectF(
                screenX - size / 2,
                canvas.height / 2 - size / 2,
                screenX + size / 2,
                canvas.height / 2 + size / 2
            )

            val bmp = when (entity) {
                is Enemy -> enemyTextures[entity.spriteId.coerceIn(0, enemyTextures.count() - 1)]
                is Prop -> propTextures[entity.spriteId.coerceIn(0, propTextures.count() - 1)]
                is PickupItem -> pickupables[entity.spriteId.coerceIn(0, pickupables.count() - 1)]
                else -> continue
            }
            val shade = ((1f - distance / 1024) * 255f).toInt().coerceIn(0, 255)
            val paint = Paint()
            paint.colorFilter = LightingColorFilter(Color.rgb(shade, shade, shade), 0)
            paint.isFilterBitmap = false
            canvas.drawBitmap(bmp, null, dst, paint)
        }
    }
    /*fun drawEnemies(state: GameState, canvas: Canvas) {

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
                enemy.hp == 0 -> 6
                enemy.shooting -> 5
                enemy.shootingStance -> 4
                spriteAngle > -45 && spriteAngle <= 45 -> 2     // back
                spriteAngle > 45 && spriteAngle <= 135 -> 3     // left
                spriteAngle <= -45 && spriteAngle > -135 -> 1   // right
                else -> 0                      // front
            }
            Log.d("game", sprite.toString())
            val screenX =
                (0.5f + angleDiff / fov) * screenW

            val size =
                (screenH * 64f) / distance

            val top = screenH / 2 - size / 2
            val bottom = screenH / 2 + size / 2

            val left = screenX - size / 2
            val right = screenX + size / 2f

            val dst = RectF(left, top, right, bottom)
            val paint = Paint()
            val shade = ((1f - distance / 1024) * 255f).toInt().coerceIn(0, 255)
            paint.colorFilter = LightingColorFilter(Color.rgb(shade, shade, shade), 0)
            paint.isFilterBitmap = false
            canvas.drawBitmap(enemyTextures[enemy.spriteId * 5 + sprite], null, dst, paint)
        }
    }*/

    fun drawWeapon(state: GameState, canvas: Canvas) {
        val weaponWidth = 1024f
        val weaponHeight = 1024f

        val left = (canvas.width - weaponWidth) / 2f   // center horizontally
        val top = canvas.height - weaponHeight      // bottom of screen
        val right = left + weaponWidth
        val bottom = canvas.height.toFloat()


        var spriteId = 2 * state.player.currentWeapon
        if (state.player.shooting)
            spriteId++
        canvas.drawBitmap(weaponTextures[spriteId], null, RectF(left, top, right, bottom), null)
    }
}