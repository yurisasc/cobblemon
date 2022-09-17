/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.pokemon.Natures
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource

class NatureUpdatePacket(
    private var mintNature : Boolean = false
) : StringUpdatePacket() {
    constructor(pokemon: Pokemon, value: String, mintNature: Boolean): this() {
        this.setTarget(pokemon)
        this.value = value
        this.mintNature = mintNature
    }

    override fun set(pokemon: Pokemon, value: String) {
        // Check for removing mint
        if (mintNature && value.isEmpty()) {
            pokemon.mintedNature = null
            return
        }

        val nature = Natures.getNature(cobbledResource(value))
        // Validate the nature locally
        if (nature == null) {
            LOGGER.warn("A invalid nature of '$value' was attempted to be put onto: '$pokemon'")
            return
        }

        // Check which nature to modify
        if (!mintNature) {
            pokemon.nature = nature
        } else {
            pokemon.mintedNature = nature
        }
    }
}