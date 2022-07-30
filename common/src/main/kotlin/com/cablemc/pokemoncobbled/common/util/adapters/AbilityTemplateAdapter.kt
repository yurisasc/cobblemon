package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.api.abilities.Abilities
import com.cablemc.pokemoncobbled.common.api.abilities.AbilityTemplate
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

object AbilityTemplateAdapter: JsonSerializer<AbilityTemplate>, JsonDeserializer<AbilityTemplate> {
    override fun serialize(src: AbilityTemplate, type: Type, ctx: JsonSerializationContext) = JsonPrimitive(src.name)
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext) = Abilities.getOrException(json.asString)
}