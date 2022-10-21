/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.pokemon.update

import com.cablemc.pokemod.common.api.pokeball.PokeBalls
import com.cablemc.pokemod.common.pokemon.Pokemon
import net.minecraft.util.Identifier
class CaughtBallUpdatePacket() : StringUpdatePacket() {
    constructor(pokemon: Pokemon, value: String): this() {
        this.setTarget(pokemon)
        this.value = value
    }

    override fun set(pokemon: Pokemon, value: String) {
        val pokeBall = PokeBalls.getPokeBall(Identifier(value))
        if (pokeBall != null) {
            pokemon.caughtBall = pokeBall
        }
    }
}