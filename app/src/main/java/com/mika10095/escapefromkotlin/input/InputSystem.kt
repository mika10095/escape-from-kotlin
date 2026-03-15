package com.mika10095.escapefromkotlin.input

import android.graphics.RectF
import kotlin.math.abs

class InputSystem(width: Int, height: Int) {

    val forwardButton = RectF(0f,0f,width.toFloat()/3f,height.toFloat()/2f)
    val backButton = RectF(0f,height-height.toFloat()/2f,width.toFloat()/3f,height.toFloat())
    val shootButton = RectF(width/2f,0f,width.toFloat(),height.toFloat())
    var turnInputGyro = 0f
    var turnInputGravity = 0f
    var mapInputGravity = 0f
    var movementInput = 0f
    var shootInput = false
    fun setSensorInput(gyroInput: GyroInput, tiltInput: TiltInput)
    {
        if (abs(gyroInput.yaw) > 0.1 )
        {turnInputGyro = gyroInput.yaw}
        else {turnInputGyro = 0f}
        if (abs(tiltInput.turn) > 0.05 )
        {turnInputGravity = tiltInput.turn}
        else {turnInputGravity = 0f}

        mapInputGravity = tiltInput.tilt
    }
}