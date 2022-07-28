package com.cablemc.pokemoncobbled.common.util.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.visitor.NbtOrderedStringFormatter

/**
 * An adapter that handles an [NbtCompound] using string conversion.
 *
 * @author Hiroku
 * @since July 25th, 2022
 */
object NbtCompoundAdapter : JsonDeserializer<NbtCompound>, JsonSerializer<NbtCompound> {
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext) = NbtHelper.fromNbtProviderString(json.asString)
    override fun serialize(nbt: NbtCompound, type: Type, ctx: JsonSerializationContext): JsonElement {
        return JsonPrimitive(NbtOrderedStringFormatter("", 0, mutableListOf()).apply(nbt))
    }
}