package com.cablemc.pokemoncobbled.common.client.battle.animations

import com.cablemc.pokemoncobbled.common.client.battle.ActiveClientBattlePokemon
import com.cablemc.pokemoncobbled.common.client.battle.ClientBattlePokemon

class SwapAndMoveTileOnscreenAnimation(val battlePokemon: ClientBattlePokemon, val duration: Float = 0.75F) : TileAnimation {
    var passedSeconds = 0F
    override fun shouldHoldUntilNextAnimation() = false
    override fun invoke(activeBattlePokemon: ActiveClientBattlePokemon, deltaTicks: Float): Boolean {
        if (passedSeconds == 0F) {
            activeBattlePokemon.battlePokemon = battlePokemon
        }
        passedSeconds += deltaTicks / 20
        passedSeconds = passedSeconds.coerceAtMost(duration)
        val ratio = passedSeconds / duration
        val totalMovement = activeBattlePokemon.invisibleX - activeBattlePokemon.xDisplacement
        val currentMovement = totalMovement * (1 - ratio)
        activeBattlePokemon.xDisplacement += currentMovement
        return passedSeconds == duration
    }
}