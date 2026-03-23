package com.mika10095.escapefromkotlin.ents

import com.mika10095.escapefromkotlin.engine.GameState

class PickupItem : EntityBase() {
    enum class PickupType {
        SHOTGUN,
        ROCKET,
        AMMOPISTOL,
        AMMOSHOTGUN,
        HEALTHS,
        HEALTHM,
        HEALTHL,
        ARMORS,
        ARMORL,
        TREASURES,
        TREASUREM,
        TREASUREL,
        TREASUREXL,
        KEY
    }

    val type: PickupType
        get() = when (spriteId) {
            0 -> PickupType.SHOTGUN
            1 -> PickupType.ROCKET
            2 -> PickupType.HEALTHS
            3 -> PickupType.HEALTHM
            4 -> PickupType.HEALTHL
            5 -> PickupType.ARMORS
            6 -> PickupType.ARMORL
            7 -> PickupType.AMMOPISTOL
            8 -> PickupType.AMMOSHOTGUN
            9 -> PickupType.TREASURES
            10 -> PickupType.TREASUREM
            11 -> PickupType.TREASUREL
            12 -> PickupType.TREASUREXL
            13 -> PickupType.KEY
            else -> PickupType.KEY
        }

    init {
        pickupable = true
        solid = false
        radius = 10f
    }

    override fun onPlayerTouch(state: GameState) {
        when (type) {
            PickupType.SHOTGUN -> {
                state.player.unlockShotgun = true
                state.player.weaponAmmoCurrent[2] = 5
            }

            PickupType.ROCKET -> {
                state.player.unlockPanzerfaust = true
                state.player.weaponAmmoCurrent[3] = 1
            }

            PickupType.AMMOPISTOL -> state.player.weaponAmmoCurrent[1] =
                (state.player.weaponAmmoCurrent[1] + 15).coerceIn(
                    null,
                    state.player.weaponAmmoMax[1]
                )

            PickupType.AMMOSHOTGUN -> state.player.weaponAmmoCurrent[2] =
                (state.player.weaponAmmoCurrent[2] + 10).coerceIn(
                    null,
                    state.player.weaponAmmoMax[2]
                )

            PickupType.HEALTHS -> state.player.hp =
                (state.player.hp + 15).coerceIn(null, state.player.maxhp)

            PickupType.HEALTHM -> state.player.hp =
                (state.player.hp + 30).coerceIn(null, state.player.maxhp)

            PickupType.HEALTHL -> state.player.hp =
                (state.player.hp + 60).coerceIn(null, state.player.maxhp)

            PickupType.ARMORS -> state.player.armor =
                (state.player.armor + 25).coerceIn(null, state.player.maxarmor)

            PickupType.ARMORL -> state.player.armor =
                (state.player.armor + 50).coerceIn(null, state.player.maxarmor)

            PickupType.TREASURES -> state.player.score += 1
            PickupType.TREASUREM -> state.player.score += 3
            PickupType.TREASUREL -> state.player.score += 5
            PickupType.TREASUREXL -> state.player.score += 10
            PickupType.KEY -> state.player.keyCount++
        }
        kill()
    }
}