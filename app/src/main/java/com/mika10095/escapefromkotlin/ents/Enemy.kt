package com.mika10095.escapefromkotlin.ents

import android.util.Log
import com.mika10095.escapefromkotlin.engine.GameState
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sign
import kotlin.math.sin

class Enemy(val spriteId: Int = 0,val attackRange: Float = 512f,val shootDelay: Float = 3f) : EntityBase() {

    var visible = false
    var shooting = false
    var shootingStance = false
    var shootCooldown = 0f
    var state = State.WANDER
    var lastSeenX = 0f
    var lastSeenY = 0f
    var searchTimer = 0f
    val maxSearchTime = 3f
    val viewDistance = 1024f
    val hearingRange = 256f
    val fov = Math.toRadians(90.0).toFloat()
    enum class State {
        WANDER,
        CHASE,
        SEARCH
    }

    fun update(gameState: GameState, dt: Double) {
        super.update(dt)
        updateVisibility(gameState)
        if(hp == 0)
            return
        val player = gameState.player

        val dx = player.posx - posx
        val dy = player.posy - posy
        val dist = hypot(dx.toDouble(), dy.toDouble()).toFloat()

        shootCooldown -= dt.toFloat()

        if (player.shooting && dist < hearingRange) {
            lastSeenX = player.posx
            lastSeenY = player.posy
            state = State.SEARCH
            searchTimer = maxSearchTime
        }



        if (visible) {
            lastSeenX = player.posx
            lastSeenY = player.posy
            state = State.CHASE
            searchTimer = maxSearchTime
        }

        if (state == State.SEARCH) {
            searchTimer -= dt.toFloat()
            if (searchTimer <= 0f) {
                state = State.WANDER
            }
        }

        when (state) {
            State.WANDER -> wander(gameState, dt)
            State.CHASE -> chase(gameState, dt)
            State.SEARCH -> search(gameState, dt)
        }
    }

    fun wander(gameState: GameState, dt: Double) {
        rot += (Math.random().toFloat() - 0.5f) * turnspeed * dt.toFloat() * 3f

        val moveSpeed = speed * 0.1f

        val moveX = cos(rot) * moveSpeed * dt.toFloat()
        val moveY = sin(rot) * moveSpeed * dt.toFloat()
        if (!gameState.gameMap.isWallCircle(posx + moveX, posy, radius)) {
            posx += moveX
        }
        if (!gameState.gameMap.isWallCircle(posx, posy + moveY, radius)) {
            posy += moveY
        }
    }

    fun chase(gameState: GameState, dt: Double) {
        val player = gameState.player
        val dx = player.posx - posx
        val dy = player.posy - posy

        val targetAngle = atan2(dy, dx)
        rotateTowards(targetAngle, dt)

        val dist = hypot(dx.toDouble(), dy.toDouble()).toFloat()

        val diff = angleDiff(targetAngle, rot)

        if (cos(diff.toDouble()) < 0) {
            return
        }

        val moveFactor = when {
            abs(diff) < 0.2f -> 1f
            abs(diff) < 0.6f -> 0.5f
            else -> 0.1f
        }

        if (dist > attackRange || !visible) {
            val moveX = cos(rot) * speed * moveFactor * dt.toFloat()
            val moveY = sin(rot) * speed * moveFactor * dt.toFloat()

            if (!gameState.gameMap.isWallCircle(posx + moveX, posy, radius)) {
                posx += moveX
            }
            if (!gameState.gameMap.isWallCircle(posx, posy + moveY, radius)) {
                posy += moveY
            }
        }
        Log.d("game", "Enemy angle: " + abs(Math.toDegrees(diff.toDouble())))
        // shooting
        if (dist < attackRange && abs(Math.toDegrees(diff.toDouble()))<5f && visible) {
            shootingStance = true
            if (shootCooldown <= 0f) {
                shooting = true
                shootCooldown = shootDelay
                Log.d("game", "Player got hit")
            } else {
                shooting = false
            }
        } else {
            shootingStance = false
            shooting = false
            shootCooldown = shootDelay
        }
    }
    fun search(gameState: GameState, dt: Double) {
        val dx = lastSeenX - posx
        val dy = lastSeenY - posy

        val dist = hypot(dx.toDouble(), dy.toDouble()).toFloat()

        if (dist < 10f) return

        val targetAngle = atan2(dy, dx)
        rotateTowards(targetAngle, dt)

        var diff = angleDiff(targetAngle,rot)

        if (abs(diff) < 0.5f) {
            val moveX = cos(rot) * speed * dt.toFloat()
            val moveY = sin(rot) * speed * dt.toFloat()

            if (!gameState.gameMap.isWallCircle(posx + moveX, posy, radius)) {
                posx += moveX
            }
            if (!gameState.gameMap.isWallCircle(posx, posy + moveY, radius)) {
                posy += moveY
            }
        }
    }
    fun rotateTowards(target: Float, dt: Double) {
        var diff = target - rot

        while (diff > Math.PI) diff -= (2 * Math.PI).toFloat()
        while (diff < -Math.PI) diff += (2 * Math.PI).toFloat()

        if (abs(diff) > 0.05f) {
            rot += sign(x = diff) * turnspeed * dt.toFloat()
        }
    }
    fun updateVisibility(gameState: GameState) {
        val dx = gameState.player.posx - posx
        val dy = gameState.player.posy - posy

        val dist = hypot(dx.toDouble(), dy.toDouble()).toFloat()
        if (dist > viewDistance) {
            visible = false
            return
        }

        val angleToPlayer = atan2(dy, dx)

        var diff = angleToPlayer - rot
        while (diff > Math.PI) diff -= (2 * Math.PI).toFloat()
        while (diff < -Math.PI) diff += (2 * Math.PI).toFloat()

        if (abs(diff) > fov / 2f) {
            lastSeenX = gameState.player.posx
            lastSeenY = gameState.player.posy
        }

        val hit = gameState.renderer.raycaster.castRay(
            posx,
            posy,
            angleToPlayer,
            gameState.gameMap
        )

        visible = hit.distance >= dist
    }
}
