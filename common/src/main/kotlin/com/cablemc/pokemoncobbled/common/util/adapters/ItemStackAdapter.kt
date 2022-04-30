package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.util.asNbt
import com.cablemc.pokemoncobbled.common.util.saveToJson
import com.google.gson.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.lang.reflect.Type

/**
 * Saves and loads an [ItemStack] with JSON.
 * The JSON can be either a [JsonPrimitive] or a [JsonObject].
 * If it is a [JsonPrimitive] the expected value is simply the [Identifier] of the item.
 * If it is a [JsonObject] the entire [CompoundTag] behind the [ItemStack] is expected.
 *
 * When serializing it will always convert the equivalent [CompoundTag] into a [JsonObject]
 *
 * @author Licious
 * @since March 20th, 2022
 */
object ItemStackAdapter : JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ItemStack = (if (json.isJsonPrimitive) ItemStack(Registry.ITEM.get(Identifier(json.asString.lowercase()))) else ItemStack.fromNbt(json.asNbt() as NbtCompound))!!

    override fun serialize(src: ItemStack, typeOfSrc: Type, context: JsonSerializationContext) = src.saveToJson()

}