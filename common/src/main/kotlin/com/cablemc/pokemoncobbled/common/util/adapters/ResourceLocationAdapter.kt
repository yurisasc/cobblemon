package com.cablemc.pokemoncobbled.common.util.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.minecraft.resources.ResourceLocation
import java.lang.reflect.Type

/**
 * Basic string adapter for [ResourceLocation]s.
 *
 * @author Hiroku
 * @since January 24th, 2022
 */
object ResourceLocationAdapter : JsonSerializer<ResourceLocation>, JsonDeserializer<ResourceLocation> {
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext) = ResourceLocation(json.asString)
    override fun serialize(src: ResourceLocation, type: Type, ctx: JsonSerializationContext) = JsonPrimitive(src.toString())
}