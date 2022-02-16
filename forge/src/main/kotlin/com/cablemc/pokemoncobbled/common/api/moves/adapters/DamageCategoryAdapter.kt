package com.cablemc.pokemoncobbled.common.api.moves.adapters

import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategories
import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategory
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

object DamageCategoryAdapter: JsonSerializer<DamageCategory>, JsonDeserializer<DamageCategory> {

    override fun serialize(src: DamageCategory, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.name)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DamageCategory {
        return DamageCategories.getOrException(json.asString)
    }
}