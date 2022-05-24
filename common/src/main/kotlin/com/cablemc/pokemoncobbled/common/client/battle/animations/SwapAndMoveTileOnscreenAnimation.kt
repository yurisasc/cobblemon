package com.cablemc.pokemoncobbled.common.client.battle.animations

import com.cablemc.pokemoncobbled.common.client.battle.ActiveClientBattlePokemon
import com.cablemc.pokemoncobbled.common.client.battle.ClientBattlePokemon

class SwapAndMoveTileOnscreenAnimation(val battlePokemon: ClientBattlePokemon, val duration: Float = 0.5F) : TileAnimation {
    var passedSeconds = 0F
    override fun invoke(activeBattlePokemon: ActiveClientBattlePokemon, deltaTicks: Float): Boolean {
        if (passedSeconds == 0F) {
            activeBattlePokemon.battlePokemon = battlePokemon
        }
        passedSeconds += deltaTicks
        passedSeconds = passedSeconds.coerceAtMost(duration)
        val ratio = duration / passedSeconds
        val totalMovement = activeBattlePokemon.xDisplacement - activeBattlePokemon.invisibleX
        val currentMovement = totalMovement * ratio
        activeBattlePokemon.xDisplacement += currentMovement
        return passedSeconds == duration
    }
}