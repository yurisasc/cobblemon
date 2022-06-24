package com.cablemc.pokemoncobbled.common.config.starter

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.minecraft.text.Text
import java.lang.reflect.Type

object StarterCategoryAdapter : JsonSerializer<StarterCategory>, JsonDeserializer<StarterCategory> {
    override fun serialize(src: StarterCategory, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val json = JsonObject()
        json.addProperty("name", src.name)
        json.addProperty("displayName", Text.Serializer.toJson(src.displayName))
        val pokemon = JsonObject()
        src.pokemon.forEachIndexed { index, pokemonProperties ->
            pokemon.add(index.toString(), pokemonProperties.saveToJSON())
        }
        json.add("pokemon", pokemon)
        return json
    }

    override fun deserialize(jsonIn: JsonElement, typeOfT: Type, context: JsonDeserializationContext): StarterCategory {
        val json = jsonIn.asJsonObject

        val name = json.get("name").asString
        val displayName = Text.Serializer.fromJson(json.get("displayName").asString)!!
        val pokemon = mutableListOf<PokemonProperties>()
        with(json.getAsJsonObject("pokemon")) {
            for (i in 0 until this.size()) {
                pokemon.add(PokemonProperties().loadFromJSON(this.get(i.toString()).asJsonObject))
            }
        }

        return StarterCategory(
            name = name,
            displayName = displayName,
            pokemon = pokemon
        )
    }
}