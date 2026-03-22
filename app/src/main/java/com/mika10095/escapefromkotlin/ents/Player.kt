package com.mika10095.escapefromkotlin.ents

import android.util.Log
import com.mika10095.escapefromkotlin.engine.GameState
import com.mika10095.escapefromkotlin.input.InputSystem
import kotlin.math.cos
import kotlin.math.sin

class Player: EntityBase() {
    var shooting = false
    var startedShooting = 0.0
    fun update(state: GameState,inputSystem: InputSystem, dt: Double) {
        super.update(dt)
        if(state.gameMap.tileAtFromWorld(posx+40*cos(rot),posy+40*sin(rot))==state.gameMap.tiles.DOOR && inputSystem.shootInput){
            state.gameMap.setTileAtFromWorld(posx+40*cos(rot),posy+40*sin(rot),state.gameMap.tiles.DOOR_OPEN)
            inputSystem.shootInput = false
        }
        if(state.gameMap.tileAtFromWorld(posx+40*cos(rot),posy+40*sin(rot))==state.gameMap.tiles.SECRET_DOOR && inputSystem.shootInput){
            state.gameMap.setTileAtFromWorld(posx+40*cos(rot),posy+40*sin(rot),state.gameMap.tiles.SECRET_DOOR_OPEN)
            inputSystem.shootInput = false
        }
        if(state.gameMap.tileAtFromWorld(posx+40*cos(rot),posy+40*sin(rot))==state.gameMap.tiles.EXIT && inputSystem.shootInput){
            state.resetLevel()
            inputSystem.shootInput = false
        }
        else if (inputSystem.shootInput && startedShooting < 0)
        {
            startedShooting = 0.25
            shooting = true
            state.playerShoot()
            inputSystem.shootInput = false
        }
        startedShooting-=dt
        Log.d("shooting","Shooting delay left: $startedShooting + are we shooting? $shooting")
        if(startedShooting < 0)
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
        rot += inputSystem.turnInputGravity * dt.toFloat() * state.settingsManager.tiltAimSens
        rot -= inputSystem.turnInputGyro * dt.toFloat() *  state.settingsManager.gyroAimSens
        rot += inputSystem.turnInput * dt.toFloat() *  state.settingsManager.buttonAimSens
    }


}