package com.mika10095.escapefromkotlin.engine

import com.mika10095.escapefromkotlin.engine.map.GameMap

class RayCaster {

    fun castRay(
        px: Float,
        py: Float,
        angle: Float,
        map: GameMap
    ): Float {

        val dirX = kotlin.math.cos(angle)
        val dirY = kotlin.math.sin(angle)

        val tile = map.tileSize

        val relX = px - map.posX
        val relY = py - map.posY

        var mapX = (relX / tile).toInt()
        var mapY = (relY / tile).toInt()

        val deltaDistX = kotlin.math.abs(tile / dirX)
        val deltaDistY = kotlin.math.abs(tile / dirY)

        var sideDistX: Float
        var sideDistY: Float

        var stepX: Int
        var stepY: Int

        if (dirX < 0) {
            stepX = -1
            sideDistX = (relX - mapX * tile) / -dirX
        } else {
            stepX = 1
            sideDistX = ((mapX + 1) * tile - relX) / dirX
        }

        if (dirY < 0) {
            stepY = -1
            sideDistY = (relY - mapY * tile) / -dirY
        } else {
            stepY = 1
            sideDistY = ((mapY + 1) * tile - relY) / dirY
        }

        var hit = false
        var side = 0

        while (!hit) {

            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX
                mapX += stepX
                side = 0
            } else {
                sideDistY += deltaDistY
                mapY += stepY
                side = 1
            }

            if (map.isWall(mapX, mapY))
                hit = true
        }

        return if (side == 0)
            sideDistX - deltaDistX
        else
            sideDistY - deltaDistY
    }
}