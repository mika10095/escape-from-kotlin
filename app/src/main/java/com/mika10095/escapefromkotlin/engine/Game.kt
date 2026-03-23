package com.mika10095.escapefromkotlin.engine

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DrawFilter
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.mika10095.escapefromkotlin.R
import com.mika10095.escapefromkotlin.engine.GameState.StateEnum
import com.mika10095.escapefromkotlin.engine.map.Renderer
import com.mika10095.escapefromkotlin.input.GyroInput
import com.mika10095.escapefromkotlin.input.InputSystem
import com.mika10095.escapefromkotlin.input.TiltInput

class Game(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    var gameThread: GameThread? = null
    val settingsManager = SettingsManager(context)
    val renderer = Renderer(context)
    val gameState: GameState = GameState(settingsManager, renderer)
    lateinit var inputSystem: InputSystem
    lateinit var gyroInput: GyroInput
    lateinit var tiltInput: TiltInput

    var shootPointerId: Int? = null

    init {
        gameState.gameMap.setMap(32,29, arrayOf(
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,
            0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,1,0,0,0,0,0,2,
            0,0,0,0,0,0,0,2,2,2,2,2,0,1,0,0,0,0,0,0,0,0,0,-2,0,4,0,0,0,0,0,2,
            0,1,1,1,1,1,1,2,0,0,0,2,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,2,
            1,1,0,0,0,0,1,1,1,4,1,1,1,1,0,0,0,0,0,0,0,0,1,1,1,1,2,2,0,0,0,2,
            1,0,0,0,-2,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0,0,0,0,2,0,0,0,2,
            1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0,0,0,0,2,0,0,0,2,
            1,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,1,1,1,1,0,2,0,0,0,2,
            1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,2,0,0,0,2,
            1,0,0,-2,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,-2,0,1,0,2,0,0,0,2,
            1,1,0,0,0,0,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,0,2,0,0,0,2,
            0,1,1,4,1,1,1,0,1,0,0,0,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,4,1,1,
            0,0,2,0,2,0,0,0,1,0,0,0,1,1,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,1,
            1,1,1,4,1,1,0,0,1,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,-2,0,0,0,
            1,-202,0,0,0,1,1,1,1,0,0,0,1,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,
            1,0,0,0,-120,1,0,0,1,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
            1,0,-1,0,0,3,0,0,3,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,-2,0,0,0,
            1,0,0,0,-121,1,0,-122,1,0,0,0,1,0,0,0,0,0,0,3,0,-2,0,0,0,0,0,0,0,0,0,0,
            1,0,0,0,0,1,4,1,1,0,0,0,1,0,-2,0,0,-2,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
            1,4,1,1,1,1,0,0,1,1,3,1,1,1,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,1,
            -200,0,0,0,-217,2,0,-205,1,0,0,0,1,1,1,0,0,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,
            -201,0,0,0,-216,2,0,-207,1,0,0,0,1,1,1,4,1,1,1,1,0,0,0,0,1,0,0,0,1,0,0,0,
            -202,0,0,0,-215,2,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1,1,0,-2,0,1,1,0,0,
            -203,0,0,0,-214,2,0,0,1,0,0,0,0,-2,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,
            -204,0,0,0,-213,2,0,0,1,1,0,0,0,0,0,1,2,2,2,2,2,2,1,1,0,0,0,0,0,1,1,0,
            -205,0,0,0,-212,2,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,-125,1,0,0,-100,0,0,-100,0,1,0,
            -206,0,0,0,-211,2,0,0,0,0,0,0,-112,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,1,0,
            -207,0,0,0,-210,2,2,0,0,0,0,0,-112 ,0,0,-127,2,2,2,2,2,2,1,1,0,0,0,0,0,1,1,0,
            -208,-112,-112,-112,-209,0,2,2,2,2,2,2,2,2,2,2,2,2,0,0,0,0,0,1,1,1,5,1,1,1,0,0)
        )
        if (gameState.gameMap.map.size != gameState.gameMap.width * gameState.gameMap.height) {
            throw IllegalArgumentException("Map array size ${gameState.gameMap.map.size} does not match width*height (${gameState.gameMap.width*gameState.gameMap.height})")
        }
        gameState.resetLevel()
        holder.addCallback(this)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        inputSystem = InputSystem(context,settingsManager, width, height)
        gyroInput = GyroInput(context)
        tiltInput = TiltInput(context)
        gyroInput.start()
        tiltInput.start()
        gameThread = GameThread(holder, this)
        gameThread?.running = true
        gameThread?.start()
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        gyroInput.stop()
        tiltInput.stop()
        gameThread?.running = false
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawColor(Color.BLACK)
        gameState.drawState(canvas)
        if(settingsManager.debug)
            drawDebug(canvas)
        drawUI(canvas)
        update()
    }
    fun drawUI(canvas: Canvas){
        val paint = Paint()
        paint.setTypeface(Typeface.create("Arial",Typeface.BOLD));
        paint.color = Color.argb(0.25f,1f,1f,1f)
        if(gameState.currentState != StateEnum.MENU){
        canvas.drawBitmap(inputSystem.menuButtonSprite,null,inputSystem.menuButton, paint)
        canvas.drawBitmap(inputSystem.mapButtonSprite,null,inputSystem.mapButton, paint)}
        if(gameState.currentState == StateEnum.MAP){
            if(inputSystem.requestedWeapon == 0)
                paint.color = Color.argb(1f,1f,1f,1f)
            canvas.drawBitmap(inputSystem.knifeButtonSprite,null,inputSystem.knifeButton, paint)
            paint.color = Color.argb(0.25f,1f,1f,1f)
            if(inputSystem.requestedWeapon == 1)
                paint.color = Color.argb(1f,1f,1f,1f)
            canvas.drawBitmap(inputSystem.pistolButtonSprite,null,inputSystem.pistolButton, paint)
            paint.color = Color.argb(0.25f,1f,1f,1f)
            if(gameState.player.shotgunUnlocked){
                if(inputSystem.requestedWeapon == 2)
                    paint.color = Color.argb(1f,1f,1f,1f)
                canvas.drawBitmap(inputSystem.shotgunButtonSprite,null,inputSystem.shotgunButton, paint)}
            paint.color = Color.argb(0.25f,1f,1f,1f)
            if(gameState.player.panzerfaustUnlocked){
                if(inputSystem.requestedWeapon == 3)
                    paint.color = Color.argb(1f,1f,1f,1f)
                canvas.drawBitmap(inputSystem.launcherButtonSprite,null,inputSystem.launcherButton, paint)
            }
            paint.color = Color.argb(0.25f,1f,1f,1f)
            }
        else if(gameState.currentState == StateEnum.GAME) {
            paint.color = Color.argb(0.25f,1f,1f,1f)
            canvas.drawBitmap(inputSystem.healthIcon,null,inputSystem.healthIconHolder,paint)
            canvas.drawBitmap(inputSystem.armorIcon,null,inputSystem.armorIconHolder,paint)
            canvas.drawBitmap(inputSystem.ammoIcon,null,inputSystem.ammoIconHolder,paint)
            canvas.drawBitmap(inputSystem.moveLeftSprite,null,inputSystem.turnLeftButton, paint)
            canvas.drawBitmap(inputSystem.moveRightSprite,null,inputSystem.turnRightButton, paint)
            paint.color = Color.WHITE
            paint.textSize = 60f
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("time: " + gameState.levelTimer.toInt().toString() + "\t score: " + gameState.player.score.toString(),canvas.width/2f,80f,paint)
            canvas.drawText(gameState.player.hp.toString(),inputSystem.healthIconHolder.centerX(),inputSystem.healthIconHolder.centerY(),paint)
            canvas.drawText(gameState.player.armor.toString(),inputSystem.armorIconHolder.centerX(),inputSystem.armorIconHolder.centerY(),paint)
            canvas.drawText(gameState.player.weaponAmmoCurrent[gameState.player.currentWeapon].toString(),inputSystem.ammoIconHolder.centerX(),inputSystem.ammoIconHolder.centerY(),paint)

        }
    }

    fun drawDebug(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.MAGENTA
        paint.textSize = 40f
        canvas.drawText(
            "FPS: " + gameThread?.fps.toString() + "\tUpdateMS: " + gameThread?.updateMili.toString() + "\t DT: " + gameThread?.dt.toString(),
            20f,
            100f,
            paint
        )
        var x =  20f
        var y =   150f
        for (line in inputSystem.debugText().split("\n")){
            canvas.drawText(line,x,y,paint)
            y += paint.descent() - paint.ascent();
        }
    }

    fun update() {
        gameThread?.let {
            inputSystem.setSensorInput(gyroInput, tiltInput)
            gameState.updateState(it.dt, inputSystem)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {

                val index = event.actionIndex
                val pointerId = event.getPointerId(index)
                val x = event.getX(index)
                val y = event.getY(index)

                if (inputSystem.shootButton.contains(x, y) && shootPointerId == null) {
                    //stupid shenanigans for such an important button...
                    shootPointerId = pointerId
                    inputSystem.pressedShoot()
                }
                if (inputSystem.forwardButton.contains(x, y)) {
                    inputSystem.movementInput = 1f
                } else if (inputSystem.backButton.contains(x, y)) {
                    inputSystem.movementInput = -1f
                }
                if (inputSystem.turnLeftButton.contains(x, y)) {
                    inputSystem.turnInput = -1f
                } else if (inputSystem.turnRightButton.contains(x, y)) {
                    inputSystem.turnInput = 1f
                }
                if (inputSystem.mapButton.contains(x, y)) {
                    inputSystem.mapInputScreen = !inputSystem.mapInputScreen
                }
                if (inputSystem.menuButton.contains(x, y)) {
                    inputSystem.menuInput = true
                }
                if(gameState.currentState == StateEnum.MAP) {
                    if (inputSystem.knifeButton.contains(x, y)) {
                        inputSystem.requestedWeapon = 0
                    } else if (inputSystem.pistolButton.contains(x, y)) {
                        inputSystem.requestedWeapon = 1
                    } else if (inputSystem.shotgunButton.contains(x, y)) {
                        inputSystem.requestedWeapon = 2
                    } else if (inputSystem.launcherButton.contains(x, y)) {
                        inputSystem.requestedWeapon = 3
                    }
                    Log.d("input", "selected weapon ${inputSystem.requestedWeapon} player's weapon ${gameState.player.currentWeapon}")
                    if(inputSystem.requestedWeapon == 2 && !gameState.player.shotgunUnlocked)
                    {
                        inputSystem.requestedWeapon = 0
                    }
                    if(inputSystem.requestedWeapon == 3 && !gameState.player.panzerfaustUnlocked)
                    {
                        inputSystem.requestedWeapon = 0
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                var forward = false
                var back = false
                var left = false
                var right = false

                for (i in 0 until event.pointerCount) {
                    val x = event.getX(i)
                    val y = event.getY(i)

                    if (inputSystem.forwardButton.contains(x, y)) forward = true
                    if (inputSystem.backButton.contains(x, y)) back = true
                    if (inputSystem.turnLeftButton.contains(x, y)) left = true
                    if (inputSystem.turnRightButton.contains(x, y)) right = true
                }
                inputSystem.movementInput = when {
                    forward -> 1f
                    back -> -1f
                    else -> 0f
                }
                inputSystem.turnInput = when {
                    right -> 1f
                    left -> -1f
                    else -> 0f
                }

                shootPointerId?.let { id ->
                    val index = event.findPointerIndex(id)

                    if (index == -1) {
                        inputSystem.releasedShoot()
                        shootPointerId = null
                    } else {
                        val x = event.getX(index)
                        val y = event.getY(index)

                        if (!inputSystem.shootButton.contains(x, y)) {
                            inputSystem.releasedShoot()
                            shootPointerId = null
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_CANCEL -> {

                val index = event.actionIndex
                val pointerId = event.getPointerId(index)
                val x = event.getX(index)
                val y = event.getY(index)

                if (pointerId == shootPointerId) {
                    inputSystem.releasedShoot()
                    shootPointerId = null
                }
                if (inputSystem.forwardButton.contains(x, y) ||
                    inputSystem.backButton.contains(x, y)
                ) {
                    inputSystem.movementInput = 0f
                }
                if (inputSystem.turnLeftButton.contains(x, y) ||
                    inputSystem.turnRightButton.contains(x, y)
                ) {
                    inputSystem.turnInput = 0f
                }
            }
        }

        return true
    }
}