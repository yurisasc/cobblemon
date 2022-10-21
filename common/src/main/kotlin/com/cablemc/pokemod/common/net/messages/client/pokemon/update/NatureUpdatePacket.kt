/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.pokemon.update

import com.cablemc.pokemod.common.Pokemod.LOGGER
import com.cablemc.pokemod.common.api.pokemon.Natures
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.util.pokemodResource
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

        val nature = Natures.getNature(pokemodResource(value))
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