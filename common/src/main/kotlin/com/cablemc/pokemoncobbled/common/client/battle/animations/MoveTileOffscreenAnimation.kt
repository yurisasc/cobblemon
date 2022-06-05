package com.cablemc.pokemoncobbled.common.client.battle.animations

import com.cablemc.pokemoncobbled.common.client.battle.ActiveClientBattlePokemon

class MoveTileOffscreenAnimation(private val duration: Float = 0.5F) : TileAnimation {
    var passedSeconds = 0F
    override fun invoke(activeBattlePokemon: ActiveClientBattlePokemon, deltaTicks: Float): Boolean {
        passedSeconds += deltaTicks / 20
        passedSeconds = passedSeconds.coerceAtMost(duration)
        val ratio = duration / passedSeconds
        val totalMovement = activeBattlePokemon.invisibleX - activeBattlePokemon.xDisplacement
        val currentMovement = totalMovement * ratio
        activeBattlePokemon.xDisplacement += currentMovement
        return passedSeconds == duration
    }
}