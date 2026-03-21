package com.mika10095.escapefromkotlin.ents

import com.mika10095.escapefromkotlin.engine.GameState
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

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

            val targetAngle = atan2(dy, dx)

            val diff = targetAngle - rot
            if(abs(PI.toFloat()-abs(diff))>0.05f){
                shooting = false
                if (diff < 0) {
                    rot += turnspeed * dt.toFloat()
                } else {
                    rot -= turnspeed * dt.toFloat()
                }
            }
            if(attackRange < sqrt(dx.toDouble()*dy))
                shooting = true
        }
    }

    fun updateVisibility(gameState: GameState)
    {
        val dx = posx - gameState.player.posx
        val dy = posy - gameState.player.posy

        val angle = atan2(dy, dx)
        val distToEnemy = sqrt(dx*dx + dy*dy)
        // uhh, this raycast line is kinda ugly I should do something about it
        val hit = gameState.renderer.raycaster.castRay(
            gameState.player.posx,
            gameState.player.posy,
            angle,
            gameState.gameMap
        )

        visible = hit.distance >= distToEnemy
    }
}