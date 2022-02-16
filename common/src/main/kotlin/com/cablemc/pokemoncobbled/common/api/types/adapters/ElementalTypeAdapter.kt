package com.cablemc.pokemoncobbled.common.api.types.adapters

import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.ElementalTypes
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

object ElementalTypeAdapter: JsonSerializer<ElementalType>, JsonDeserializer<ElementalType> {
    override fun serialize(src: ElementalType, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.name)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ElementalType {
        return ElementalTypes.getOrException(json.asString)
    }
}