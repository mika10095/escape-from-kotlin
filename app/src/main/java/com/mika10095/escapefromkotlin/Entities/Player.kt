package com.mika10095.escapefromkotlin.Entities

class Player {
    var posx = 0f
    var posy = 0f
    var rot = 0f
    var radius = 20f

    fun setPosition(x: Float,y: Float)
    {
        posx = x
        posy = y
    }
}