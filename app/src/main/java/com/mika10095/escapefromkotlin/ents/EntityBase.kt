package com.mika10095.escapefromkotlin.ents

import android.util.Log

@Suppress("SpellCheckingInspection")
abstract class EntityBase{
    var hp = 100
    val dead get() = hp == 0
    var posx = 0f
    var posy = 0f
    var rot = 0f
        set(value)
        {
            var angleRad = value
            while (angleRad > Math.PI) angleRad -= (2 * Math.PI).toFloat()
            while (angleRad < -Math.PI) angleRad += (2 * Math.PI).toFloat()
            field = angleRad
        }

    var speed = 100f
    var radius = 15f
    var turnspeed = 2f

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
}