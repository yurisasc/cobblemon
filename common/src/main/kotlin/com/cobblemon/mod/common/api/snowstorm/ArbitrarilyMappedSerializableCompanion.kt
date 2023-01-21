package com.cobblemon.mod.common.api.snowstorm

import com.cobblemon.mod.common.api.codec.CodecMapped
import com.cobblemon.mod.common.api.codec.MappedCodec
import com.mojang.serialization.Codec
import net.minecraft.network.PacketByteBuf

abstract class ArbitrarilyMappedSerializableCompanion<T : CodecMapped, K>(
    val keyFromString: (String) -> K,
    val stringFromKey: (K) -> String,
    val keyFromValue: (T) -> K
) {
    val codec: MappedCodec<T, K> = MappedCodec(codecRetriever = { subtypes[it]!!.codec }, keyFromString = keyFromString)
    private val subtypes = mutableMapOf<K, RegisteredSubtype<out T>>()

    fun <E : T> registerSubtype(key: K, clazz: Class<E>, codec: Codec<E>) {
        subtypes[key] = RegisteredSubtype(clazz, codec)
    }

    fun writeToBuffer(buffer: PacketByteBuf, value: T) {
        val typeString = stringFromKey(keyFromValue(value))
        buffer.writeString(typeString)
        value.writeToBuffer(buffer)
    }

    fun readFromBuffer(buffer: PacketByteBuf): T {
        val typeString = buffer.readString()
        val clazz = subtypes[keyFromString(typeString)]!!.clazz
        val value = clazz.getDeclaredConstructor().newInstance()
        value.readFromBuffer(buffer)
        return value
    }
}

class RegisteredSubtype<T>(val clazz: Class<out T>, val codec: Codec<out T>)