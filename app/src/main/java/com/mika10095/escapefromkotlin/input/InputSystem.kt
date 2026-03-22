package com.mika10095.escapefromkotlin.input

import android.graphics.RectF
import kotlin.math.abs

class InputSystem(width: Int, height: Int) {

    val forwardButton = RectF(0f,0f,width.toFloat()/3f,height.toFloat()/2f)
    val backButton = RectF(0f,height-height.toFloat()/2f,width.toFloat()/3f,height.toFloat())
    val shootButton = RectF(width/2f,0f,width.toFloat(),height.toFloat()*(2f/3f))
    val turnLeftButton = RectF(width/2f,height.toFloat()*(2f/3f),width.toFloat()*(3f/4f),height.toFloat())
    val turnRightButton = RectF(width.toFloat()*(3f/4f),height.toFloat()*(2f/3f),width.toFloat(),height.toFloat())
    val mapButton = RectF(width.toFloat()*(3f/4f),0f,width.toFloat()*(7f/8f),height.toFloat()*(1f/8f))
    val menuButton = RectF(width.toFloat()*(7f/8f),0f,width.toFloat(),height.toFloat()*(1f/8f))
    var turnInputGyro = 0f
    var turnInputGravity = 0f
    var turnInput = 0f
    val mapInput get() = mapInputScreen || mapInputGravity
    var mapInputScreen = false
    var mapInputGravity = false
    
    var menuInput = false
    var movementInput = 0f
    var shootInput = false
    var shootInputReset = true

    fun pressedShoot() {
        if (shootInputReset) {
            shootInput = true
            shootInputReset = false
        }
    }

    fun releasedShoot() {
        shootInput = false
        shootInputReset = true
    }

    fun setSensorInput(gyroInput: GyroInput, tiltInput: TiltInput)
    {
        turnInputGyro = 0f
        turnInputGravity = 0f
        if (abs(gyroInput.yaw) > 0.1 )
        {turnInputGyro = gyroInput.yaw}
        if (abs(tiltInput.turn) > 0.05 )
        {turnInputGravity = tiltInput.turn}
        if(tiltInput.tilt < 1f)
            mapInputGravity = true
        if(tiltInput.tilt >= 1f)
            mapInputGravity = false
    }
    fun clearInputs()
    {
        turnInputGyro = 0f
        turnInputGravity = 0f
        turnInput = 0f
        movementInput = 0f
        shootInput = false
        mapInputGravity = false
        mapInputScreen = false
        menuInput = false
        shootInputReset = true
    }
}