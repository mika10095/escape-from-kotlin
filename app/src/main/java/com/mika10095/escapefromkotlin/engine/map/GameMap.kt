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
    fun setTileAt(x: Int, y: Int, value: Int) {

        if (x < 0 || y < 0 || x >= width || y >= height)
            return

        map[y * width + x] = value
    }
    fun setTileAtFromWorld(x: Float, y: Float, value: Int) {
        val mapX = ((x - posX) / tileSize).toInt()
        val mapY = ((y - posY) / tileSize).toInt()

        setTileAt(mapX, mapY, value)
    }
    fun tileAt(x: Int, y: Int): Int {

        if (x < 0 || y < 0 || x >= width || y >= height)
            return 0

        return map[y * width + x]
    }
    fun tileAtFromWorld(x: Float, y: Float): Int {
        val mapX = ((x - posX) / tileSize).toInt()
        val mapY = ((y - posY) / tileSize).toInt()

        return tileAt(mapX, mapY)
    }
    fun isWall(x: Int, y: Int): Boolean {

        if (x < 0 || y < 0 || x >= width || y >= height)
            return true

        return map[y * width + x] > 0
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