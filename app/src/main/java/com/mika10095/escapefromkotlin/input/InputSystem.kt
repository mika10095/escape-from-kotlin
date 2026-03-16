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
    var mapInput = false
    var menuInput = false
    var movementInput = 0f
    var shootInput = false

    fun setSensorInput(gyroInput: GyroInput, tiltInput: TiltInput)
    {
        turnInputGyro = 0f
        turnInputGravity = 0f
        if (abs(gyroInput.yaw) > 0.1 )
        {turnInputGyro = gyroInput.yaw}
        if (abs(tiltInput.turn) > 0.05 )
        {turnInputGravity = tiltInput.turn}
        if(tiltInput.tilt < 0.1)
            mapInput = true
        if(tiltInput.tilt >= 1f)
            mapInput = false
    }
}