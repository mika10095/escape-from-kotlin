package com.mika10095.escapefromkotlin.ents

import android.util.Log
import android.util.Log.*
import com.mika10095.escapefromkotlin.engine.GameState
import com.mika10095.escapefromkotlin.input.InputSystem
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

class Player : EntityBase() {
    override var hp = 50
    var score = 0
    var maxhp = 100
    var maxarmor = 100
    var armor = 0
    var keyCount = 0
    var shooting = false
    var startedShooting = 0.0
    var currentWeapon = 0
    var shotgunUnlocked = false
    var unlockShotgun = false
    var panzerfaustUnlocked = false
    var unlockPanzerfaust = false

    var weaponRanges = arrayOf(
        124f,
        1024f,
        512f,
        1024f
    )
    var weaponDamageBase = arrayOf(
        50,
        20,
        40,
        200
    )
    var weaponAmmoCurrent = arrayOf(
        1,
        15,
        0,
        0
    )
    var weaponAmmoMax = arrayOf(
        0, //you'll never find knife ammo...
        30,
        10,
        1
    )
    var weaponDelay = arrayOf(
        0.5,
        0.25,
        0.5,
        99.0
    )

    fun update(state: GameState, inputSystem: InputSystem, dt: Double) {
        super.update(state, dt)
        if (unlockShotgun) {
            d("weapon", "Player picked up Shotgun")
            shotgunUnlocked = true
            inputSystem.requestedWeapon = 2
            unlockShotgun = false
        }
        if (unlockPanzerfaust) {
            d("weapon", "Player picked up Panzerfaust")
            panzerfaustUnlocked = true
            inputSystem.requestedWeapon = 3
            unlockPanzerfaust = false
        }
        if (inputSystem.requestedWeapon != currentWeapon) {
            if (currentWeapon == 3 && weaponAmmoCurrent[3] == 0) {
                panzerfaustUnlocked = false
            }
            d(
                "game",
                "Player switching weapon from: $currentWeapon to ${inputSystem.requestedWeapon}"
            )
            currentWeapon = inputSystem.requestedWeapon
            startedShooting = 0.0
        }
        if (state.gameMap.tileAtFromWorld(
                posx + 40 * cos(rot),
                posy + 40 * sin(rot)
            ) == state.gameMap.tiles.DOOR && inputSystem.shootInput
        ) {
            state.gameMap.setTileAtFromWorld(
                posx + 40 * cos(rot),
                posy + 40 * sin(rot),
                state.gameMap.tiles.DOOR_OPEN
            )
            inputSystem.shootInput = false
        }
        if (state.gameMap.tileAtFromWorld(
                posx + 40 * cos(rot),
                posy + 40 * sin(rot)
            ) == state.gameMap.tiles.SECRET_DOOR && inputSystem.shootInput
        ) {
            state.gameMap.setTileAtFromWorld(
                posx + 40 * cos(rot),
                posy + 40 * sin(rot),
                state.gameMap.tiles.SECRET_DOOR_OPEN
            )
            inputSystem.shootInput = false
        }
        if (state.gameMap.tileAtFromWorld(
                posx + 40 * cos(rot),
                posy + 40 * sin(rot)
            ) == state.gameMap.tiles.EXIT && inputSystem.shootInput
        ) {
            state.resetLevel()
            inputSystem.shootInput = false
        } else if (inputSystem.shootInput && startedShooting < 0) {
            startedShooting = weaponDelay[currentWeapon]
            shooting = true
            playerShoot(state, inputSystem)
            inputSystem.shootInput = false
        }
        startedShooting -= dt
        //Log.d("shooting","Shooting delay left: $startedShooting + are we shooting? $shooting")
        if (startedShooting < 0) {
            shooting = false
        }
        val moveX = cos(rot) * inputSystem.movementInput * dt.toFloat() * speed
        val moveY = sin(rot) * inputSystem.movementInput * dt.toFloat() * speed
        state.tryMoveEntity(this, moveX, moveY)
        rot += inputSystem.turnInputGravity * dt.toFloat() * state.settingsManager.tiltAimSens
        rot -= inputSystem.turnInputGyro * dt.toFloat() * state.settingsManager.gyroAimSens
        rot += inputSystem.turnInput * dt.toFloat() * state.settingsManager.buttonAimSens
    }

    fun playerShoot(state: GameState, inputSystem: InputSystem) {
        if (weaponAmmoCurrent[currentWeapon] <= 0) {
            inputSystem.requestedWeapon = 0
            shooting = false
            return
        }
        val px = posx
        val py = posy
        val angle = rot
        //ew I hate this line...
        val rayHit = state.renderer.raycaster.castRay(
            px,
            py,
            angle,
            state.gameMap
        )

        var closestEnemy: EntityBase? = null
        var closestDist = Float.MAX_VALUE

        for (enemy in state.entities) {
            if (enemy !is Enemy) {
                continue
            }
            val dx = enemy.posx - px
            val dy = enemy.posy - py

            val dist = hypot(dx.toDouble(), dy.toDouble()).toFloat()

            val dot = dx * cos(angle) + dy * sin(angle)

            if (dot < 0) continue

            val perpDist = abs(
                dx * sin(angle) - dy * cos(angle)
            )
            d(
                "shooting",
                "distance to enemy $dist, current weapon's range ${weaponRanges[currentWeapon]} "
            )
            if (perpDist < enemy.radius && enemy.hp > 0) {
                if (dist < rayHit.distance && dist < closestDist) {
                    if (dist < weaponRanges[currentWeapon]) {
                        closestDist = dist
                        closestEnemy = enemy
                    }
                }
            }
        }
        if (currentWeapon != 0)
            weaponAmmoCurrent[currentWeapon]--
        val damage = ((1 - (Random.nextFloat() - 0.5f)) * weaponDamageBase[currentWeapon]).toInt()
        d("shooting", "Shooting for $damage, Ammmo left ${weaponAmmoCurrent[currentWeapon]}")
        closestEnemy?.takeDamage(damage)
    }

}
