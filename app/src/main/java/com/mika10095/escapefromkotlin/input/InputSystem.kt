package com.mika10095.escapefromkotlin.input

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.RectF
import com.mika10095.escapefromkotlin.R
import com.mika10095.escapefromkotlin.engine.SettingsManager
import kotlin.math.abs

class InputSystem(context: Context,var settingsManager: SettingsManager, width: Int, height: Int) {

    val forwardButton = RectF(0f,0f,width.toFloat()/3f,height.toFloat()/2f)
    val backButton = RectF(0f,height-height.toFloat()/2f,width.toFloat()/3f,height.toFloat())
    val shootButton = RectF(width/2f,0f,width.toFloat(),height.toFloat()*(3f/4f))
    val turnLeftButton = RectF(width/2f,height.toFloat()*(3f/4f),width.toFloat()*(3f/4f),height.toFloat())
    val turnRightButton = RectF(width.toFloat()*(3f/4f),height.toFloat()*(3f/4f),width.toFloat(),height.toFloat())
    val mapButton = RectF(width.toFloat()*(3f/4f),0f,width.toFloat()*(7f/8f),width.toFloat()*(7f/8f)-width.toFloat()*(3f/4f))
    val menuButton = RectF(width.toFloat()*(7f/8f),0f,width.toFloat(),width.toFloat()*(7f/8f)-width.toFloat()*(3f/4f))
    val moveLeftSprite = BitmapFactory.decodeResource(context.resources, R.drawable.move_left)
    val moveRightSprite = BitmapFactory.decodeResource(context.resources, R.drawable.move_right)
    val mapButtonSprite = BitmapFactory.decodeResource(context.resources, R.drawable.map)
    val menuButtonSprite = BitmapFactory.decodeResource(context.resources, R.drawable.exit_button)
    val knifeButton = RectF(0f,0f,height.toFloat()/4f,height.toFloat()/4)
    val pistolButton = RectF(0f,height.toFloat()/4,height.toFloat()/4f,2*height.toFloat()/4)
    val shotgunButton = RectF(0f,2*height.toFloat()/4,height.toFloat()/4f,3*height.toFloat()/4)
    val launcherButton = RectF(0f,3*height.toFloat()/4,height.toFloat()/4f,height.toFloat())
    val knifeButtonSprite = BitmapFactory.decodeResource(context.resources, R.drawable.knife_icon)
    val pistolButtonSprite = BitmapFactory.decodeResource(context.resources, R.drawable.pistol_icon)
    val shotgunButtonSprite = BitmapFactory.decodeResource(context.resources, R.drawable.shotgun_icon)
    val launcherButtonSprite = BitmapFactory.decodeResource(context.resources, R.drawable.panzerfaust_icon)
    val healthIconHolder = RectF(0f,3f/4f*height.toFloat(),height.toFloat()/4f,height.toFloat())
    val armorIconHolder = RectF(height.toFloat()/4f,3f/4f*height.toFloat(),2*height.toFloat()/4f,height.toFloat())
    val ammoIconHolder = RectF(2*height.toFloat()/4f,3f/4f*height.toFloat(),3*height.toFloat()/4f,height.toFloat())
    val healthIcon = BitmapFactory.decodeResource(context.resources, R.drawable.health_icon)
    val armorIcon = BitmapFactory.decodeResource(context.resources, R.drawable.armor_icon)
    val ammoIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ammo_icon)
    var requestedWeapon = 1
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
        if(settingsManager.gyroAimSens == 0f)
            turnInputGyro = 0f
        if(settingsManager.tiltAimSens == 0f){
            turnInputGravity = 0f
            mapInputGravity = false
        }

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
    fun resetWeapon()
    {
        requestedWeapon = 1
    }
    fun debugText(): String
    {
        return("GyroTurn = $turnInputGyro \n " +
                "TiltTurn = $turnInputGravity \n" +
                "ButtonTurn = $turnInput \n" +
                "Movement = $movementInput \n" +
                "Shoot = $shootInput Reset = $shootInputReset \n" +
                "Menu = $menuInput Map = $mapInput MapGravity = $mapInputGravity \n ")
    }
}