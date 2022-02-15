package com.cablemc.pokemoncobbled.common.util.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * Adapts an IntRange into a simple hyphenated integer pair string. IntRange(2, 4) is serialized as
 * "2-4", and one-element ranges are serialized as single integers such that IntRange(10, 10) which
 * serializes as "10".
 *
 * Because of the hyphen separator, this adapter should never be used for ranges that can be negative.
 *
 * @author Hiroku
 * @since February 14th, 2022
 */
object PositiveIntRangeAdapter : JsonSerializer<IntRange>, JsonDeserializer<IntRange> {
    override fun serialize(range: IntRange, type: Type, ctx: JsonSerializationContext): JsonElement {
        return if (range.first == range.last) {
            JsonPrimitive(range.first)
        } else {
            JsonPrimitive("${range.first}-${range.last}")
        }
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): IntRange {
        val splits = json.asString.split("-")
        return if (splits.size == 1) {
            splits[0].toInt().let { IntRange(it, it) }
        } else {
            IntRange(splits[0].toInt(), splits[1].toInt())
        }
    }
}