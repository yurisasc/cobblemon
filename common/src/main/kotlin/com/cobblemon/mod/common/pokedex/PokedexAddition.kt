/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokedex

import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.Species
import net.minecraft.util.Identifier

/**
 * Contains a list of [PokedexEntry]
 */
class PokedexAddition {
    var displayName = "cobblemon.pokedex.undefined"
    var pokemon = mutableListOf<PokedexEntry>()

    /**
     * Provides the [PokedexAddition] number of the requested [Species] or returns -1
     */
    fun getPokedexNumber(species: Species): Int {
        var entry: PokedexEntry? = null
        pokemon.forEach() {
            if (it.matchSpecies(species)) {
                entry = it
            }
        }
        return pokemon.indexOf(entry)
    }

    fun getPokedexNumber(pokemon: Pokemon): Int {
        var entry: PokedexEntry? = null
        this.pokemon.forEach() {
            if (it.matchSpecies(pokemon)) {
                entry = it
            }
        }
        return this.pokemon.indexOf(entry)
    }

    /**
     * Flattens [pokemon] to have forms in the same [PokedexEntry]
     */
    fun flattenDex() {
        var lastEntry = PokedexEntry(Identifier("cobblemon:nulldex"), arrayListOf())
        val flatEntries = arrayListOf<PokedexEntry>()
        pokemon.forEach() {
            if(!it.isValidEntry()) {
                return
            }
            if(it.matchSpecies(lastEntry)) {
                lastEntry.formNames.addAll(it.formNames)
            } else {
                lastEntry = it
            }
            if(lastEntry.species != Identifier("cobblemon:nulldex") && !flatEntries.contains(lastEntry)) {
                flatEntries.add(lastEntry)
            }
        }
        pokemon = flatEntries
    }
}