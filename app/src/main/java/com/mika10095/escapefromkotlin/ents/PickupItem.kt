package com.mika10095.escapefromkotlin.ents

import com.mika10095.escapefromkotlin.engine.GameState

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

class PickupItem : EntityBase() {

    lateinit var type: PickupType

    init {
        pickupable = true
        solid = false
        radius = 14f
    }

    override fun onPlayerTouch(state: GameState) {
        when (type) {
            PickupType.SHOTGUN -> state.player.shotgunUnlocked = true
            PickupType.ROCKET -> {
                state.player.shotgunUnlocked = true
                state.player.weaponAmmoCurrent[3] = 1
            }
            PickupType.AMMOPISTOL -> state.player.weaponAmmoCurrent[1] += 15
            PickupType.AMMOSHOTGUN -> state.player.weaponAmmoCurrent[2] += 10
            PickupType.HEALTHS -> state.player.hp = (state.player.hp+15).coerceIn(null, state.player.maxhp)
            PickupType.HEALTHM -> state.player.hp = (state.player.hp+30).coerceIn(null, state.player.maxhp)
            PickupType.HEALTHL -> state.player.hp = (state.player.hp+60).coerceIn(null, state.player.maxhp)
            PickupType.ARMORS -> state.player.armor = (state.player.armor+25).coerceIn(null, state.player.maxarmor)
            PickupType.ARMORL -> state.player.armor = (state.player.armor+50).coerceIn(null, state.player.maxarmor)
            PickupType.TREASURES -> state.player.score += 1
            PickupType.TREASUREM -> state.player.score += 3
            PickupType.TREASUREL -> state.player.score += 5
            PickupType.TREASUREXL -> state.player.score += 10
            PickupType.KEY ->  state.player.keyCount++
        }
        kill()
    }
}