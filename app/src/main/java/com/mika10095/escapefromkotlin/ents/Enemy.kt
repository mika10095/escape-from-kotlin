package com.mika10095.escapefromkotlin.ents

import com.mika10095.escapefromkotlin.engine.GameState
import kotlin.math.PI
import kotlin.math.abs

class Enemy : EntityBase() {
    var spriteId = 0
    var attackRange = 100f
    var visible = false
    var shooting = false

    fun update(gameState: GameState, dt: Double)
    {
        updateVisibility(gameState)
        if(visible)
        {
            val dx = posx - gameState.player.posx
            val dy = posy - gameState.player.posy

            val targetAngle = kotlin.math.atan2(dy, dx)

            var diff = targetAngle - rot
            while (diff > Math.PI) diff -= (2 * Math.PI).toFloat()
            while (diff < -Math.PI) diff += (2 * Math.PI).toFloat()
            if(abs(PI.toFloat()-abs(diff))>0.05f){
                shooting = false
                if (diff < 0) {
                    rot += turnspeed * dt.toFloat()
                } else {
                    rot -= turnspeed * dt.toFloat()
                }
            }
            else
                shooting = true
        }
    }

    fun updateVisibility(gameState: GameState)
    {
        val dx = posx - gameState.player.posx
        val dy = posy - gameState.player.posy

        val angle = kotlin.math.atan2(dy, dx)
        val distToEnemy = kotlin.math.sqrt(dx*dx + dy*dy)

        val hit = gameState.renderer.raycaster.castRay(
            gameState.player.posx,
            gameState.player.posy,
            angle,
            gameState.gameMap
        )

        visible = hit.distance >= distToEnemy
    }
}