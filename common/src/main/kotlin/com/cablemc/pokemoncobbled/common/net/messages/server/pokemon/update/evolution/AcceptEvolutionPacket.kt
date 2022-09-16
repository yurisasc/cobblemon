/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.server.pokemon.update.evolution

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class AcceptEvolutionPacket() : EvolutionDisplayUpdatePacket() {

    constructor(pokemon: Pokemon, evolution: EvolutionDisplay): this() {
        this.setTarget(pokemon)
        this.current = evolution
    }

    override fun applyToPokemon(pokemon: Pokemon) {
        val evolution = pokemon.evolutionProxy.server().firstOrNull { evolution -> evolution.id.equals(this.evolutionId, true) } ?: return
        pokemon.evolutionProxy.server().start(evolution)
    }
}