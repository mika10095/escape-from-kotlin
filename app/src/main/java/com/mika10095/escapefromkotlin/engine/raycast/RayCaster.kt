package com.mika10095.escapefromkotlin.engine.raycast

import com.mika10095.escapefromkotlin.engine.map.GameMap
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class RayCaster {

    fun castRay(
        px: Float,
        py: Float,
        angle: Float,
        map: GameMap
    ): RayHit {

        val dirX = cos(angle)
        val dirY = sin(angle)

        val tile = map.tileSize

        val relX = px - map.posX
        val relY = py - map.posY

        var mapX = (relX / tile).toInt()
        var mapY = (relY / tile).toInt()

        val deltaDistX = abs(tile / dirX)
        val deltaDistY = abs(tile / dirY)

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



        val perpDist =
            if (side == 0)
                sideDistX - deltaDistX
            else
                sideDistY - deltaDistY

        val wallX =
            if (side == 0)
                (relY + perpDist * dirY) / tile
            else
                (relX + perpDist * dirX) / tile

        val wallXFrac = wallX - floor(wallX)

        return RayHit(
            perpDist,
            wallXFrac,
            map.map[mapY * map.width + mapX],
            side
        )
    }
}