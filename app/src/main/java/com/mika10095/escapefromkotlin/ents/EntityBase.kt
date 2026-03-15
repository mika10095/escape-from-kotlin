package com.mika10095.escapefromkotlin.ents

abstract class EntityBase{
    var posx = 0f
    var posy = 0f
    var rot = 0f
    var speed = 100f
    var radius = 15f
    var turnspeed = 2f

    fun setPosition(x: Float,y: Float)
    {
        posx = x
        posy = y
    }
}