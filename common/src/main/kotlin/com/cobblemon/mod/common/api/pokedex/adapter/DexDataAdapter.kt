package com.cobblemon.mod.common.api.pokedex.adapter

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.pokedex.DexData
import com.cobblemon.mod.common.pokedex.DexPokemonData
import com.google.gson.*
import net.minecraft.util.Identifier
import java.lang.reflect.Type

object DexDataAdapter: JsonDeserializer<DexData>, JsonSerializer<DexData> {
    override fun deserialize(jElement: JsonElement, type: Type, context: JsonDeserializationContext): DexData {
        val json = jElement.asJsonObject
        val pokemonList: MutableList<DexPokemonData> = mutableListOf()
        val pokedexListAsJson = json.getAsJsonArray("pokemon_list")
        if(pokedexListAsJson != null){
            for(dexPokemonDataJson in pokedexListAsJson){
                val dexPokemonData = DexPokemonDataAdapter.deserialize(dexPokemonDataJson, DexPokemonData::class.java, context)
                if(dexPokemonData.species != null) {
                    pokemonList.add(dexPokemonData)
                } else {
                    Cobblemon.LOGGER.error("Failed to load Pokedex Entry for {}.", dexPokemonData.name)
                }
            }
        }

        val containedDexesArray = json.getAsJsonArray("contained_dexes")
        val containedDexes : MutableList<Identifier> = mutableListOf()
        if(containedDexesArray != null) {
            containedDexes.addAll(containedDexesArray.map { Identifier(it.asString) })
        }

        return DexData(
            identifier = Identifier(json.get("identifier").asString),
            enabled = json.getAsJsonPrimitive("enabled")?.asBoolean ?: true,
            contained_dexes = containedDexes,
            pokemon_list = pokemonList,
            override_categories = json.getAsJsonPrimitive("override_categories")?.asBoolean ?: false
        )
    }

    override fun serialize(data: DexData, type: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(data).asJsonObject
    }
}