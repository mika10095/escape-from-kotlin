package com.mika10095.escapefromkotlin.ents

import com.mika10095.escapefromkotlin.engine.GameState
import com.mika10095.escapefromkotlin.input.InputSystem
import kotlin.math.cos
import kotlin.math.sin

class Player: EntityBase() {
    var shooting = false
    var startedshooting = 0.0
    fun update(state: GameState,inputSystem: InputSystem, dt: Double) {
        if(state.gameMap.tileAtFromWorld(posx+40*cos(rot),posy+40*sin(rot))==4 && inputSystem.shootInput){
            state.gameMap.setTileAtFromWorld(posx+40*cos(rot),posy+40*sin(rot),0)
            inputSystem.shootInput = false
        }
        else if (inputSystem.shootInput)
        {
            startedshooting = 0.2
            shooting = true
            inputSystem.shootInput = false
        }
        startedshooting-=dt
        if(startedshooting < 0)
        {
            shooting = false
        }
        val moveX = cos(rot) * inputSystem.movementInput * dt.toFloat() * speed
        val moveY = sin(rot) * inputSystem.movementInput * dt.toFloat() * speed
        if (!state.gameMap.isWallCircle(posx + moveX, posy, radius)) {
            posx += moveX
        }
        if (!state.gameMap.isWallCircle(posx, posy + moveY, radius)) {
            posy += moveY
        }
        rot += inputSystem.turnInputGravity * dt.toFloat() * turnspeed
        rot -= inputSystem.turnInputGyro * dt.toFloat() * turnspeed/2
        if (rot < 0f) rot += (2 * Math.PI).toFloat()
        if (rot > (2 * Math.PI).toFloat()) rot -= (2 * Math.PI).toFloat()
    }


}