package com.mika10095.escapefromkotlin.ents

import android.util.Log
import com.mika10095.escapefromkotlin.engine.GameState
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot

@Suppress("SpellCheckingInspection")
abstract class EntityBase(var hp: Int = 100, var posx: Float = 0f, var posy: Float = 0f, var turnspeed: Float = 2f, var speed: Float = 100f){
    val dead get() = hp == 0 && deathTimer < 0
    var deathTimer = 10f
    var radius = 20f
    var visible = true
    open var solid = false
    var pickupable = false
    var spriteId = 0
    var rot = 0f
        set(value)
        {
            var angleRad = value
            while (angleRad > Math.PI) angleRad -= (2 * Math.PI).toFloat()
            while (angleRad < -Math.PI) angleRad += (2 * Math.PI).toFloat()
            field = angleRad
        }
    fun kill()
    {
        deathTimer = -1f
        hp = 0
    }
    open fun onPlayerTouch(state: GameState) {}
    open fun onDelete(state: GameState) {}
    open fun update(state: GameState, dt : Double){
        if(hp == 0)
            deathTimer-=dt.toFloat()
        updateVisibility(state)
    }
    fun setPosition(x: Float,y: Float)
    {
        posx = x
        posy = y
    }
    fun takeDamage(damage: Int)
    {
        hp -= damage
        hp = hp.coerceIn(0,Int.MAX_VALUE)
        Log.d("game", "taken damage current HP: " + hp)
    }
    fun angleDiff(a: Float, b: Float): Float {
        var diff = a - b
        while (diff > Math.PI) diff -= (2 * Math.PI).toFloat()
        while (diff < -Math.PI) diff += (2 * Math.PI).toFloat()
        return diff
    }
    open fun updateVisibility(gameState: GameState) {
        val dx = gameState.player.posx - posx
        val dy = gameState.player.posy - posy

        val dist = hypot(dx.toDouble(), dy.toDouble()).toFloat()
        val angleToPlayer = atan2(dy, dx)

        val diff = angleDiff(angleToPlayer, rot)
        val spriteAngle = Math.toDegrees(diff.toDouble()).toFloat()

        val hit = gameState.renderer.raycaster.castRay(
            posx,
            posy,
            angleToPlayer,
            gameState.gameMap
        )

        visible = hit.distance >= dist
    }
}