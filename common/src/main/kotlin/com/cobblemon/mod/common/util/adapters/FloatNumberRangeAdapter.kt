package com.cobblemon.mod.common.util.adapters

import com.google.gson.*
import net.minecraft.predicate.NumberRange
import java.lang.reflect.Type

/**
 * A type adapter for [NumberRange.FloatRange].
 *
 * @author Licious
 * @since November 28th, 2022
 */
object FloatNumberRangeAdapter : JsonDeserializer<NumberRange.FloatRange>, JsonSerializer<NumberRange.FloatRange> {
    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): NumberRange.FloatRange = NumberRange.FloatRange.fromJson(element)
    override fun serialize(range: NumberRange.FloatRange, type: Type, context: JsonSerializationContext): JsonElement = range.toJson()
}