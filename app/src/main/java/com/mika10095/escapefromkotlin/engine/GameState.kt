package com.mika10095.escapefromkotlin.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.mika10095.escapefromkotlin.engine.map.GameMap
import com.mika10095.escapefromkotlin.engine.map.Renderer
import com.mika10095.escapefromkotlin.ents.Enemy
import com.mika10095.escapefromkotlin.ents.Player
import com.mika10095.escapefromkotlin.input.InputSystem
import kotlin.math.cos
import kotlin.math.sin

class GameState(context: Context) {
    val settingsManager = SettingsManager(context)
    val renderer = Renderer(context)
    val player = Player()
    val enemies = mutableListOf<Enemy>()
    val gameMap = GameMap(
        8, 8, arrayOf(
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 1, 1,
            1, 0, 1, 1, 4, 1, 1, 1,
            1, 0, 1, 0, 0, 0, 1, 1,
            1, 0, 1, 0, 0, 0, 0, 1,
            1, 0, 4, 0, 0, 0, 0, 1,
            1, 1, 1, 1, 1, 1, 1, 1,
        )
    )

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

    val settingsMenuItems = listOf(
        "FOV",
        "Ray Count",
        "Button Aim Sens",
        "Gyro Aim Sens",
        "Tilt Aim Sens",
        "Back"
    )

    fun init() {
        val enemy1 = Enemy()
        enemy1.setPosition(1000f, 555f)
        enemies.add(enemy1)
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
            renderer.drawEnemies(this, canvas)
            renderer.drawWeapon(this, canvas)
        }
        if (currentState == StateEnum.MAP) {
            drawMap(canvas)
            drawPlayer(canvas)
            drawEnemies(canvas)
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
            MenuType.MAIN -> mainMenuItems
            MenuType.SETTINGS -> settingsMenuItems
        }

        for (i in menuItems.indices) {

            paint.color =
                if (i == selectedMenuItem) Color.YELLOW
                else Color.GRAY

            val valueText = if (currentMenu == MenuType.SETTINGS) {
                when (i) {
                    0 -> " : ${settingsManager.fov}"
                    1 -> " : ${settingsManager.rayCount}"
                    2 -> " : ${settingsManager.buttonAimSens}"
                    3 -> " : ${settingsManager.gyroAimSens}"
                    4 -> " : ${settingsManager.tiltAimSens}"
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
        val paint = Paint()
        paint.color = Color.WHITE
        canvas.drawCircle(player.posx, player.posy, player.radius, paint)
        paint.color = Color.GREEN
        paint.strokeWidth = 5f
        canvas.drawLine(
            player.posx,
            player.posy,
            player.posx + 40 * cos(player.rot),
            player.posy + 40 * sin(player.rot),
            paint
        )
    }

    fun drawEnemies(canvas: Canvas) {
        for (i in 0..<enemies.count()) {
            val paint = Paint()
            if (enemies[i].visible)
                paint.color = Color.GREEN
            else
                paint.color = Color.RED
            canvas.drawCircle(enemies[i].posx, enemies[i].posy, enemies[i].radius, paint)
            if (enemies[i].shooting)
                paint.color = Color.RED
            else
                paint.color = Color.GREEN
            paint.strokeWidth = 5f
            canvas.drawLine(
                enemies[i].posx,
                enemies[i].posy,
                enemies[i].posx + 40 * cos(enemies[i].rot),
                enemies[i].posy + 40 * sin(enemies[i].rot),
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
                if (gameMap.map[index] == 1) {
                    paint.color = Color.RED

                    val left = gameMap.posX + x * gameMap.tileSize
                    val top = gameMap.posY + y * gameMap.tileSize
                    val right = left + gameMap.tileSize
                    val bottom = top + gameMap.tileSize

                    canvas.drawRect(left, top, right, bottom, paint)
                }
            }
        }
    }

    fun updateState(dt: Double, inputSystem: InputSystem) {
        val menuItems = when (currentMenu) {
            MenuType.MAIN -> mainMenuItems
            MenuType.SETTINGS -> settingsMenuItems

        }
        if (currentState == StateEnum.MENU) {
            menuSwitchCooldown -= dt
            //Log.d("debug","$menuSwitchCooldown")
            if (inputSystem.movementInput > 0 && menuSwitchCooldown < 0) {
                selectedMenuItem--
                if (selectedMenuItem < 0)
                    selectedMenuItem = menuItems.size - 1
                menuSwitchCooldown = 0.25
            }

            if (inputSystem.movementInput < 0 && menuSwitchCooldown < 0) {
                selectedMenuItem++
                if (selectedMenuItem >= menuItems.size)
                    selectedMenuItem = 0
                menuSwitchCooldown = 0.25
            }

            if (inputSystem.shootInput) {

                when (currentMenu) {

                    MenuType.MAIN -> {
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
                        }
                    }

                    MenuType.SETTINGS -> {
                        when (selectedMenuItem) {

                            0 -> settingsManager.fov += 5f
                            1 -> settingsManager.rayCount += 32
                            2 -> settingsManager.buttonAimSens += 0.1f
                            3 -> settingsManager.gyroAimSens += 0.1f
                            4 -> settingsManager.tiltAimSens += 0.1f

                            5 -> {
                                currentMenu = MenuType.MAIN
                                selectedMenuItem = 0
                            }
                        }
                    }
                }
            }
        }
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
            for (enemy in enemies) {
                enemy.update(this, dt)
                player.update(this, inputSystem, dt)
            }
        }
        if (currentState == StateEnum.MAP) {

        }

    }
}