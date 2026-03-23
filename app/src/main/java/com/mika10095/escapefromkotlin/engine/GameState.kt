package com.mika10095.escapefromkotlin.engine

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.mika10095.escapefromkotlin.engine.map.GameMap
import com.mika10095.escapefromkotlin.engine.map.Renderer
import com.mika10095.escapefromkotlin.ents.Enemy
import com.mika10095.escapefromkotlin.ents.EntityBase
import com.mika10095.escapefromkotlin.ents.Player
import com.mika10095.escapefromkotlin.input.InputSystem
import java.lang.Math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

class GameState(var settingsManager: SettingsManager, var renderer: Renderer) {

    var player = Player()
    val entities = mutableListOf<EntityBase>()
    val gameMap = GameMap(
        8, 8, arrayOf(
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 1, 1,
            1, 0, 1, 1, 3, 1, 1, 1,
            1, 0, 1, 0, 0, 0, 1, 1,
            1, 0, 1, 0, 0, 0, 0, 1,
            1, 0, 3, 0, 0, 0, 0, 1,
            1, 1, 1, 1, 1, 1, 1, 1,
        )
    )
    var changingMenuItem = false
    var selectedMenuItem = 0
    var menuSwitchCooldown = -1500000.0

    enum class StateEnum {
        MAP,
        GAME,
        MENU
    }

    var currentState = StateEnum.MENU

    enum class MenuType {
        MAIN,
        SETTINGS
    }

    var currentMenu = MenuType.MAIN

    val mainMenuItems = listOf(
        "Start Game",
        "Settings",
        "Quit"
    )

    val startedMenuItems = listOf(
        "Continue",
        "Restart",
        "Settings",
        "Quit"
    )

    val settingsMenuItems = listOf(
        "FOV",
        "Ray Count",
        "Button Aim Sens",
        "Gyro Aim Sens",
        "Tilt Aim Sens",
        "Debug Mode",
        "Back"
    )
    var levelTimer = 0.0
    var offsetX = 0f
    var offsetY = 0f
    fun mapInit(){
        for (y in 0 until gameMap.height) {
            for (x in 0 until gameMap.width) {
                when (gameMap.tileAt(x, y)) {
                    gameMap.tiles.ENEMY_1 -> {
                        val enemy = Enemy()
                        enemy.setPosition(
                            x * gameMap.tileSize + gameMap.tileSize/2,
                            y * gameMap.tileSize + gameMap.tileSize/2
                        )
                        enemy.rot = (Random.nextFloat()-0.5f)*2*PI.toFloat()
                        entities.add(enemy)
                    }
                    gameMap.tiles.PLAYER -> {
                        player = Player()
                        player.radius = 20f
                        player.setPosition(
                            x * gameMap.tileSize + gameMap.tileSize/2,
                            y * gameMap.tileSize + gameMap.tileSize/2
                        )
                    }
                    gameMap.tiles.DOOR_OPEN -> {
                        gameMap.setTileAt(x,y, gameMap.tiles.DOOR)
                    }
                    gameMap.tiles.SECRET_DOOR_OPEN -> {
                        gameMap.setTileAt(x,y, gameMap.tiles.SECRET_DOOR)
                    }
                }
            }
        }
    }
    fun resetLevel(){
        entities.clear()
        levelTimer = 0.0
        mapInit()
    }
    fun tryMoveEntity(entity: EntityBase, dx: Float, dy: Float): Boolean {
        var moved = false

        val nx = entity.posx + dx
        val ny = entity.posy + dy

        val canX = !isBlockedCircle(nx, entity.posy, entity.radius, entity)
        val canY = !isBlockedCircle(entity.posx, ny, entity.radius, entity)
        val canBoth = !isBlockedCircle(nx, ny, entity.radius, entity)

        when {
            canBoth -> {
                entity.posx = nx
                entity.posy = ny
                moved = true
            }
            canX -> {
                entity.posx = nx
                moved = true
            }
            canY -> {
                entity.posy = ny
                moved = true
            }
        }

        return moved
    }
    fun isBlockedCircle(x: Float, y: Float, radius: Float, ignore: EntityBase? = null): Boolean {
        if (gameMap.isWallCircle(x, y, radius)) return true

        for (e in entities) {
            if (e === ignore) continue
            if (!e.solid || e.dead) continue

            val dx = e.posx - x
            val dy = e.posy - y
            val rr = e.radius + radius

            if (dx * dx + dy * dy < rr * rr) return true
        }

        return false
    }
    fun drawState(canvas: Canvas) {
        if (currentState == StateEnum.MENU) {
            drawMenu(canvas)
            return
        }
        if (currentState == StateEnum.GAME) {
            val paint = Paint()
            paint.color = Color.rgb(75, 75, 75)
            canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height / 2f, paint)
            paint.color = Color.rgb(125, 125, 125)
            canvas.drawRect(
                0f,
                canvas.height / 2f,
                canvas.width.toFloat(),
                canvas.height.toFloat(),
                paint
            )
            renderer.draw(this, canvas)
            renderer.drawEntities(this, canvas)
            renderer.drawWeapon(this, canvas)
            if(settingsManager.debug) {
                drawMap(canvas)
                drawPlayer(canvas)
                drawEnemies(canvas)
            }
        }
        if (currentState == StateEnum.MAP) {
            drawPlayer(canvas)
            drawEnemies(canvas)
            drawMap(canvas)
        }

    }

    fun drawMenu(canvas: Canvas) {
        val paint = Paint()
        paint.textSize = 80f
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.WHITE

        canvas.drawText("Escape From Kotlin", canvas.width / 2f, 200f, paint)

        paint.textSize = 60f

        val menuItems = when (currentMenu) {
            MenuType.MAIN -> if (levelTimer > 0)
                    startedMenuItems
                else
                    mainMenuItems
            MenuType.SETTINGS -> settingsMenuItems
        }

        for (i in menuItems.indices) {

            paint.color =
                when (i) {
                    selectedMenuItem if changingMenuItem -> Color.GREEN
                    selectedMenuItem -> Color.YELLOW
                    else -> Color.GRAY
                }

            val valueText = if (currentMenu == MenuType.SETTINGS) {
                when (i) {
                    0 -> " : ${settingsManager.fov}"
                    1 -> " : ${settingsManager.rayCount}"
                    2 -> " : ${settingsManager.buttonAimSens}"
                    3 -> " : ${settingsManager.gyroAimSens}"
                    4 -> " : ${settingsManager.tiltAimSens}"
                    5 -> " : ${settingsManager.debug}"
                    else -> ""
                }
            } else ""

            canvas.drawText(
                menuItems[i] + valueText,
                canvas.width / 2f,
                400f + i * 100,
                paint
            )
        }
    }

    fun drawPlayer(canvas: Canvas) {
        offsetX = canvas.width/2 - player.posx
        offsetY = canvas.height/2 - player.posy

        val paint = Paint()
        paint.color = Color.WHITE
        canvas.drawCircle(player.posx+offsetX, player.posy+offsetY, player.radius, paint)
        paint.color = Color.GREEN
        paint.strokeWidth = 5f
        canvas.drawLine(
            player.posx + offsetX,
            player.posy + offsetY,
            player.posx + offsetX + 40 * cos(player.rot),
            player.posy + offsetY + 40 * sin(player.rot),
            paint
        )
    }

    fun drawEnemies(canvas: Canvas) {
        for (enemy in entities) {
            val paint = Paint()
            paint.color = if (enemy.visible) Color.GREEN else Color.RED

            canvas.drawCircle(enemy.posx + offsetX, enemy.posy + offsetY, enemy.radius, paint)

            paint.color = if (enemy.spriteId == 5) Color.RED else Color.GREEN
            paint.strokeWidth = 5f

            canvas.drawLine(
                enemy.posx + offsetX,
                enemy.posy + offsetY,
                enemy.posx + offsetX + 40 * cos(enemy.rot),
                enemy.posy + offsetY + 40 * sin(enemy.rot),
                paint
            )
        }
    }

    fun drawMap(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.RED
        for (y in 0..<gameMap.height) {
            for (x in 0..<gameMap.width) {
                val index = y * gameMap.width + x
                if (gameMap.isWall(x,y) && gameMap.tileAt(x,y) != gameMap.tiles.SECRET_WALL) {
                    when(gameMap.tileAt(x,y)){
                        gameMap.tiles.WALL -> paint.color = Color.WHITE
                        gameMap.tiles.SECRET_DOOR -> paint.color = Color.WHITE
                        gameMap.tiles.DOOR -> paint.color = Color.RED
                        else -> paint.color = Color.MAGENTA
                    }

                    val left = gameMap.posX + offsetX + x * gameMap.tileSize
                    val top = gameMap.posY + offsetY + y * gameMap.tileSize
                    val right = left + gameMap.tileSize
                    val bottom = top + gameMap.tileSize

                    canvas.drawRect(left, top, right, bottom, paint)
                }
            }
        }
    }

    fun updateMenu(dt: Double, inputSystem: InputSystem) {
        if ( menuSwitchCooldown > 0 ) return
        val menuItems = when (currentMenu) {
            MenuType.MAIN -> if (levelTimer > 0)
                startedMenuItems
            else
                mainMenuItems
            MenuType.SETTINGS -> settingsMenuItems
        }
        if (currentState == StateEnum.MENU) {
            //Log.d("debug","$menuSwitchCooldown")
            if (inputSystem.movementInput > 0 && !changingMenuItem) {
                selectedMenuItem--
                if (selectedMenuItem < 0)
                    selectedMenuItem = menuItems.size - 1
                menuSwitchCooldown = 0.25
            }

            if (inputSystem.movementInput < 0 && !changingMenuItem) {
                selectedMenuItem++
                if (selectedMenuItem >= menuItems.size)
                    selectedMenuItem = 0
                menuSwitchCooldown = 0.25
            }

            if (inputSystem.shootInput && currentMenu == MenuType.MAIN && levelTimer > 0.0) {
                when (selectedMenuItem) {

                    0 -> {
                        currentState = StateEnum.GAME
                    }

                    1 -> {

                        currentState = StateEnum.GAME
                        resetLevel()
                    }

                    2 -> {
                        currentMenu = MenuType.SETTINGS
                        selectedMenuItem = 0
                    }

                    3 -> {
                        android.os.Process.killProcess(android.os.Process.myPid())
                    }

                    else -> return
                }
                menuSwitchCooldown = 0.25
                inputSystem.clearInputs()
            }
            else if(inputSystem.shootInput && currentMenu == MenuType.MAIN){
                when (selectedMenuItem) {

                    0 -> {
                        currentState = StateEnum.GAME
                    }

                    1 -> {
                        currentMenu = MenuType.SETTINGS
                        selectedMenuItem = 0
                    }

                    2 -> {
                        android.os.Process.killProcess(android.os.Process.myPid())
                    }

                    else -> return
                }
                menuSwitchCooldown = 0.25
                inputSystem.clearInputs()
            }
            if (inputSystem.shootInput && currentMenu == MenuType.SETTINGS) {
                changingMenuItem = !changingMenuItem
                menuSwitchCooldown = 0.1
                if(selectedMenuItem == 6){
                    currentMenu = MenuType.MAIN
                    selectedMenuItem = 0
                    changingMenuItem = false
                }
                inputSystem.clearInputs()
            }
            if (inputSystem.movementInput != 0f && changingMenuItem && currentMenu == MenuType.SETTINGS) {
                when (selectedMenuItem) {

                    0 -> settingsManager.fov += 1f * inputSystem.movementInput
                    1 -> settingsManager.rayCount += 16 * inputSystem.movementInput.toInt()
                    2 -> settingsManager.buttonAimSens += ((0.1f * inputSystem.movementInput)* 10f) / 10f
                    3 -> settingsManager.gyroAimSens += ((0.1f * inputSystem.movementInput)* 10f) / 10f
                    4 -> settingsManager.tiltAimSens += ((0.1f * inputSystem.movementInput)* 10f) / 10f
                    5 -> settingsManager.debug = inputSystem.movementInput > 0
                    else ->{
                            currentMenu = MenuType.MAIN
                            selectedMenuItem = 0
                    }
                }
                menuSwitchCooldown = 0.1
            }

        }

    }

    fun updateState(dt: Double, inputSystem: InputSystem) {
        if(levelTimer == 0.0){
            inputSystem.resetWeapon()
            inputSystem.clearInputs()
        }

        levelTimer += dt
        menuSwitchCooldown -= dt
        updateMenu(dt, inputSystem)

        if (inputSystem.mapInput && currentState == StateEnum.GAME) {
            currentState = StateEnum.MAP
        }
        if (currentState == StateEnum.MAP && !inputSystem.mapInput) {
            currentState = StateEnum.GAME
            inputSystem.clearInputs()
        }
        if (inputSystem.menuInput) {
            currentState = StateEnum.MENU
            inputSystem.clearInputs()
        }
        if (currentState == StateEnum.GAME) {
            player.update(this, inputSystem, dt)
            for (entity in entities) {
                entity.update(this, dt)
            }
            for (entity in entities) {
                if (entity.pickupable) {
                    val dx = entity.posx - player.posx
                    val dy = entity.posy - player.posy
                    val dist = hypot(dx.toDouble(), dy.toDouble())

                    if (dist < entity.radius + player.radius) {
                        entity.onPlayerTouch(this)
                    }
                }
            }
            entities.removeAll { it.dead }


        }
        if (currentState == StateEnum.MAP) {

        }

    }
}