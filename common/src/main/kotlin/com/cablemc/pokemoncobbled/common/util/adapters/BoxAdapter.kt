package com.cablemc.pokemoncobbled.common.util.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type
import net.minecraft.util.math.Box

object BoxAdapter : JsonDeserializer<Box> {
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): Box {
        json as JsonObject
        return Box(
            json.get("minX")?.asDouble ?: -9999999.0,
            json.get("minY")?.asDouble ?: 0.0,
            json.get("minZ")?.asDouble ?: -9999999.0,
            json.get("maxX")?.asDouble ?: 9999999.0,
            json.get("maxY")?.asDouble ?: 9999999.0,
            json.get("maxZ")?.asDouble ?: 9999999.0
        )
    }
}