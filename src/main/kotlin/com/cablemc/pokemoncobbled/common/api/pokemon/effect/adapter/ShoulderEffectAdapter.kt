package com.cablemc.pokemoncobbled.common.api.pokemon.effect.adapter

import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffectRegistry
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

object ShoulderEffectAdapter: JsonDeserializer<ShoulderEffect>, JsonSerializer<ShoulderEffect> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ShoulderEffect {
        return ShoulderEffectRegistry.get(json.asJsonObject.get("type").asString)?.newInstance()?.deserialize(json.asJsonObject)
            ?: throw NoSuchElementException()
    }

    override fun serialize(src: ShoulderEffect, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val json = JsonObject()
        json.addProperty("type", ShoulderEffectRegistry.getName(src::class.java))
        return src.serialize(json)
    }
}