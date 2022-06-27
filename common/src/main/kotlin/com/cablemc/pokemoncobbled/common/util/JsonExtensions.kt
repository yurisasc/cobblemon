package com.cablemc.pokemoncobbled.common.util

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtOps

@JvmName("toJsonArrayString")
fun Collection<String>.toJsonArray(): JsonArray {
    val array = JsonArray()
    if (isEmpty())
        return array
    forEach { array.add(it) }
    return array
}

@JvmName("toJsonArrayBoolean")
fun Collection<Boolean>.toJsonArray(): JsonArray {
    val array = JsonArray()
    if (isEmpty())
        return array
    forEach { array.add(it) }
    return array
}

@JvmName("toJsonArrayNumber")
fun Collection<Number>.toJsonArray(): JsonArray {
    val array = JsonArray()
    if (isEmpty())
        return array
    forEach { array.add(it) }
    return array
}

@JvmName("toJsonArrayJsonElement")
fun Collection<JsonElement>.toJsonArray(): JsonArray {
    val array = JsonArray()
    if (isEmpty())
        return array
    forEach { array.add(it) }
    return array
}

fun JsonObject.isEmpty() = size() <= 0

fun JsonObject.isNotEmpty() = size() > 0

fun JsonElement.asNbt(): NbtElement = JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, this)