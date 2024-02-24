/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokedex

import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import com.cobblemon.mod.common.api.events.pokemon.TradeCompletedEvent
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionCompleteEvent
import com.cobblemon.mod.common.api.pokedex.trackeddata.SpeciesTrackedData
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.util.Identifier

/**
 * Information about a species in the dex
 *
 * @author Apion
 * @since February 24, 2024
 */
class SpeciesPokedexEntry {
    var formEntries = mutableMapOf<String, FormPokedexEntry>()
    val speciesStats = mutableSetOf<SpeciesTrackedData>()

    fun pokemonCaught(event: PokemonCapturedEvent) {
        val formStr = event.pokemon.form.formOnlyShowdownId()
        if (!formEntries.containsKey(formStr)) {
            formEntries[formStr] = FormPokedexEntry()
        }
        formEntries[formStr]?.knowledge = PokedexProgress.CAUGHT
    }

    fun pokemonEvolved(event: EvolutionCompleteEvent) {

    }

    fun pokemonTraded(event: TradeCompletedEvent) {

    }

    fun pokemonSeen(speciesId: Identifier, formStr: String) {

    }
}