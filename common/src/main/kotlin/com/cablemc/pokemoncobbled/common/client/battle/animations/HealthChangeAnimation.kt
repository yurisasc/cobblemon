package com.cablemc.pokemoncobbled.common.client.battle.animations

import com.cablemc.pokemoncobbled.common.client.battle.ActiveClientBattlePokemon

class HealthChangeAnimation(val newHealthRatio: Float, val duration: Float = 1F) : TileAnimation {
    var passedSeconds = 0F
    var initialHealth = -1F
    var difference = 0F

    override fun shouldHoldUntilNextAnimation() = false
    override fun invoke(activeBattlePokemon: ActiveClientBattlePokemon, deltaTicks: Float): Boolean {
        val pokemon = activeBattlePokemon.battlePokemon ?: return true
        if (initialHealth == -1F) {
            initialHealth = pokemon.hpRatio
            difference = newHealthRatio - initialHealth
        }

        passedSeconds += deltaTicks / 20
        passedSeconds = passedSeconds.coerceAtMost(duration)
        pokemon.hpRatio = initialHealth + (passedSeconds / duration) * difference
        return passedSeconds == duration
    }
}