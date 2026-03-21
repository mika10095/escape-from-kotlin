package com.mika10095.escapefromkotlin.ents

@Suppress("SpellCheckingInspection")
abstract class EntityBase{
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
}