/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.pokemon.update.evolution

import com.cablemc.pokemod.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemod.common.pokemon.Pokemon

class AddEvolutionPacket() : EvolutionUpdatePacket() {

    constructor(pokemon: Pokemon, evolution: Evolution): this() {
        this.setTarget(pokemon)
        this.current = evolution
        this.sending = this.createSending(pokemon)
    }

    override fun applyToPokemon(pokemon: Pokemon) {
        pokemon.evolutionProxy.client().add(this.sending)
    }

}