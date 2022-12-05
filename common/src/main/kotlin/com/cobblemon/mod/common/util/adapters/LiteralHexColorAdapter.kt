package com.cobblemon.mod.common.util.adapters

import com.google.gson.*
import java.awt.Color
import java.lang.reflect.Type

/**
 * A type adapter meant to parse a hex string color code.
 *
 * @author Licious
 * @since December 5th, 2022
 */
object LiteralHexColorAdapter : JsonDeserializer<Color>, JsonSerializer<Color> {
    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext) = Color(element.asString.removePrefix("#").toInt(16))

    override fun serialize(color: Color, type: Type, context: JsonSerializationContext) = JsonPrimitive("#${color.rgb.toString(16)}")
}