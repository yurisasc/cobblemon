/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokedex

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.Species
import net.minecraft.util.Identifier

class PokedexEntry(var species: Identifier, var formNames: MutableList<String>) {

    fun matchSpecies(entry: PokedexEntry): Boolean {
        return species.path.equals(entry.species.path, true)
    }

    fun matchSpecies(species: Species): Boolean {
        return this.species.path.equals(species.name, true)
    }

    fun matchSpecies(pokemon: Pokemon): Boolean {
        if(species.path.equals(pokemon.species.name, true)) {
            if(formNames.contains(pokemon.form.name.lowercase())) {
                return true
            }
        }
        return false
    }

    fun isValidEntry(): Boolean {
        return PokemonSpecies.getByIdentifier(species) != null
    }
}