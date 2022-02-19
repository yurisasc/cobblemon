package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.api.spawning.context.RegisteredSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * Adapter to a serialized [RegisteredSpawningContext] name to the actual registered object.
 *
 * @since January 28th, 2022
 * @author Hiroku
 */
object RegisteredSpawningContextAdapter : JsonSerializer<RegisteredSpawningContext<*>>, JsonDeserializer<RegisteredSpawningContext<*>> {
    override fun serialize(rctx: RegisteredSpawningContext<*>, type: Type, ctx: JsonSerializationContext) = JsonPrimitive(rctx.name)
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext) = SpawningContext.getByName(json.asString)
        ?: throw IllegalArgumentException("No such spawning context: ${json.asString}")
}