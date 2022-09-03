package com.cablemc.pokemoncobbled.common.api.pokemon.effect.adapter

import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffectRegistry
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

object ShoulderEffectAdapter: JsonDeserializer<ShoulderEffect> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ShoulderEffect {
        val (typeId, obj) = if (json.isJsonPrimitive) {
            json.asString to JsonObject()
        } else {
            json.asJsonObject.get("type").asString to json.asJsonObject
        }

        return context.deserialize(obj, ShoulderEffectRegistry.get(typeId))
    }
}