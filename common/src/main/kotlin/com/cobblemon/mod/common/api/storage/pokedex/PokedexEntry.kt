package com.cobblemon.mod.common.api.storage.pokedex

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Species
import com.google.gson.JsonObject
import net.minecraft.util.Identifier

class PokedexEntry(final val id: Identifier, var progressMap: MutableMap<String, DexStats> = mutableMapOf<String, DexStats>()) {
    @Transient
    var species: Species? = PokemonSpecies.getByIdentifier(id)

    init {
        if(species == null){
            Cobblemon.LOGGER.warn("Species {} is null, this Pokedex Entry may not work properly. Check if Pokemon was registered.", id)
        }
    }


    fun getStats(formString: String): DexStats = progressMap.getOrDefault(formString, DexStats())

    fun pokemonEncountered(formStr: String, isWild: Boolean) {
        val stats = getStats(formStr)
        if (isWild) {
            stats.numEncounteredWild++
        }
        else {
            stats.numEncounteredBattle++
        }
        if (!progressMap.containsKey(formStr)) {
            progressMap[formStr] = stats
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
    }
}