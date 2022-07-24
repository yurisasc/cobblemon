package com.cablemc.pokemoncobbled.common.api.serialization

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Simple adapter which expects a String and will turn into an object based on some resolver.
 *
 * @author Hiroku
 * @since July 18th, 2022
 */
class StringIdentifiedObjectAdapter<T>(val fromString: (String) -> T) : JsonDeserializer<T> {
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext) = fromString(json.asString)
}