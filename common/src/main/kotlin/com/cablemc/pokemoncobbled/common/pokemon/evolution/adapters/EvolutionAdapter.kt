package com.cablemc.pokemoncobbled.common.pokemon.evolution.adapters

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.google.gson.*
import java.lang.reflect.Type

object EvolutionAdapter : JsonDeserializer<Evolution>, JsonSerializer<Evolution> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Evolution {
        TODO("Not yet implemented")
    }

    override fun serialize(src: Evolution, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        TODO("Not yet implemented")
    }

}