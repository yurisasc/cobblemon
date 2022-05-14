package com.cablemc.pokemoncobbled.common.util.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.minecraft.util.Identifier
import java.lang.reflect.Type

/**
 * Basic string adapter for [Identifier]s.
 *
 * @author Hiroku
 * @since January 24th, 2022
 */
object IdentifierAdapter : JsonSerializer<Identifier>, JsonDeserializer<Identifier> {
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext) = Identifier(json.asString)
    override fun serialize(src: Identifier, type: Type, ctx: JsonSerializationContext) = JsonPrimitive(src.toString())
}