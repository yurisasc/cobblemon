/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.storage.pokedex

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokedex.DexStats
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Species
import com.google.gson.JsonObject
import net.minecraft.util.Identifier

class PokedexEntry(val id: Identifier, var progressMap: MutableMap<String, DexStats> = mutableMapOf()) {
    @Transient
    var species: Species? = PokemonSpecies.getByIdentifier(id)

    init {
        if(species == null){
            Cobblemon.LOGGER.warn("Species {} is null, this Pokedex Entry may not work properly. Check if Pokemon was registered.", id)
        }
    }


    fun getStats(formString: String): DexStats = progressMap.getOrDefault(formString, DexStats())

    fun incrementStats(formString: String, statToIncrement: (DexStats) -> (Unit)){

        val stats = getStats(formString)
        statToIncrement(stats)
        if (!progressMap.containsKey(formString)) {
            progressMap[formString] = stats
        }
    }

    fun saveToJSON(json: JsonObject): JsonObject {
        val featuresJson = JsonObject()
        progressMap.forEach{ (formString, dexStats) ->
            featuresJson.add(formString, dexStats.saveToJson(JsonObject()))
        }
        return json
    }

    fun loadFromJson(json: JsonObject): PokedexEntry {
        json.asMap().forEach{ (formString, dexStatsJson) ->
            progressMap[formString] = DexStats().loadFromJson(dexStatsJson.asJsonObject)
        }

        return this
    }

    companion object {
        fun formToFormString(form: FormData, shiny: Boolean): String = if (shiny) form.name + "_shiny" else form.name

        fun wildPokemonEncountered(pokedexEntry: PokedexEntry, formString: String){
            pokedexEntry.incrementStats(formString, DexStats::incrementNumEncounteredWild)
        }

        fun pokemonCaught(pokedexEntry: PokedexEntry, formString: String){
            pokedexEntry.incrementStats(formString, DexStats::incrementNumCaught)
        }

        fun pokemonEncounteredBattle(pokedexEntry: PokedexEntry, formString: String){
            pokedexEntry.incrementStats(formString, DexStats::incrementNumEncounteredBattle)
        }
    }
}