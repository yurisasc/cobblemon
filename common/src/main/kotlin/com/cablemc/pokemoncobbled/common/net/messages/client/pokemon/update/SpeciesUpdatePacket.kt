/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.Species
import net.minecraft.util.Identifier

class SpeciesUpdatePacket() : StringUpdatePacket() {
    constructor(pokemon: Pokemon, species: Species): this() {
        setTarget(pokemon)
        value = species.resourceIdentifier.toString()
    }

    // TODO: Proper check
    override fun set(pokemon: Pokemon, value: String) { pokemon.species = PokemonSpecies.getByIdentifier(Identifier(value))!! }

}