/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.battle.animations

import com.cablemc.pokemod.common.client.battle.ActiveClientBattlePokemon

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