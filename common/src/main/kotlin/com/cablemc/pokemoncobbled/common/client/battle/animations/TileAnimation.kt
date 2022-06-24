package com.cablemc.pokemoncobbled.common.client.battle.animations

import com.cablemc.pokemoncobbled.common.client.battle.ActiveClientBattlePokemon

interface TileAnimation {
    /** Returns true if the animation is done. */
    operator fun invoke(activeBattlePokemon: ActiveClientBattlePokemon, deltaTicks: Float): Boolean
    fun shouldHoldUntilNextAnimation(): Boolean
}