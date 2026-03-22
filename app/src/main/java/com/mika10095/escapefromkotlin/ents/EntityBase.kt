package com.mika10095.escapefromkotlin.ents

import android.util.Log

@Suppress("SpellCheckingInspection")
abstract class EntityBase(var hp: Int = 100, var posx: Float = 0f, var posy: Float = 0f, var turnspeed: Float = 2f, var speed: Float = 100f){
    val dead get() = hp == 0 && deathTimer < 0
    var deathTimer = 10f
    var radius = 20f
    var rot = 0f
        set(value)
        {
            var angleRad = value
            while (angleRad > Math.PI) angleRad -= (2 * Math.PI).toFloat()
            while (angleRad < -Math.PI) angleRad += (2 * Math.PI).toFloat()
            field = angleRad
        }

    fun update(dt : Double){
        if(hp == 0)
            deathTimer-=dt.toFloat()
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
}