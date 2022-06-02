package com.cablemc.pokemoncobbled.common.api.entity

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.minecraft.entity.EntityDimensions
import java.lang.reflect.Type

/**
 * An adapter for [EntityDimensions]. This isn't technically needed on newer versions of Gson because
 * they're actually pretty clever now, but Minecraft still is still packing the old version.
 *
 * @author Hiroku
 * @since April 4th, 2022
 */
object EntityDimensionsAdapter : JsonSerializer<EntityDimensions>, JsonDeserializer<EntityDimensions> {
    const val WIDTH = "width"
    const val HEIGHT = "height"

    override fun serialize(dimensions: EntityDimensions, type: Type, ctx: JsonSerializationContext): JsonElement {
        val json = JsonObject()
        json.addProperty(WIDTH, dimensions.width)
        json.addProperty(HEIGHT, dimensions.height)
        return json
    }

    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): EntityDimensions {
        json as JsonObject
        return EntityDimensions(json.get(WIDTH).asFloat, json.get(HEIGHT).asFloat, false)
    }
}