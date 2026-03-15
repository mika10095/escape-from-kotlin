package com.mika10095.escapefromkotlin.engine.map

class GameMap(x:Int, y:Int,mapvalues: Array<Int>) {
    var width  = x
    var height  = y
    var tileSize = 64f
    val mapSize get() = (width*height)
    var map: Array<Int> = mapvalues
    var posX = 0f
    var posY = 0f
    fun setPosition(x: Float,y: Float)
    {
        posX = x
        posY = y
    }
    fun isWall(x: Int, y: Int): Boolean {

        if (x < 0 || y < 0 || x >= width || y >= height)
            return true

        return map[y * width + x] == 1
    }
    fun isWallFromWorld(x: Float, y: Float): Boolean {
        val mapX = ((x - posX) / tileSize).toInt()
        val mapY = ((y - posY) / tileSize).toInt()

        return isWall(mapX, mapY)
    }
    fun isWallCircle(x: Float, y: Float, radius: Float): Boolean {

        if (isWallFromWorld(x - radius, y - radius)) return true
        if (isWallFromWorld(x + radius, y - radius)) return true
        if (isWallFromWorld(x - radius, y + radius)) return true
        if (isWallFromWorld(x + radius, y + radius)) return true

        return false
    }
}