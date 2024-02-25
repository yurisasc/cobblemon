/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokedex.adapter

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokedex.trackeddata.CountTypeCaughtGlobalTrackedData
import com.cobblemon.mod.common.api.pokedex.trackeddata.GlobalTrackedData
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import java.lang.reflect.Type
import kotlin.reflect.KClass

object GlobalTrackedDataAdapter : JsonSerializer<GlobalTrackedData>, JsonDeserializer<GlobalTrackedData> {
    private val VARIANT = "variant"
    private val types = hashMapOf<String, KClass<out GlobalTrackedData>>()
    private val decoders = hashMapOf<Identifier, (PacketByteBuf) -> (GlobalTrackedData)>()

    init {
        this.register(CountTypeCaughtGlobalTrackedData::class, CountTypeCaughtGlobalTrackedData.ID, CountTypeCaughtGlobalTrackedData::decode)
    }

    fun register(type: KClass<out GlobalTrackedData>, identifier: Identifier, decodeFunction: (PacketByteBuf) -> (GlobalTrackedData)) {
        val existing = this.types.put(identifier.toString(), type)
        if (existing != null) {
            Cobblemon.LOGGER.debug("Replaced {} under ID {} with {} in the {}", existing::class.qualifiedName, identifier.toString(), type.qualifiedName, this::class.qualifiedName)
        }
        decoders[identifier] = decodeFunction
    }

    override fun deserialize(jElement: JsonElement, type: Type, context: JsonDeserializationContext): GlobalTrackedData {
        val result = JsonOps.INSTANCE.withDecoder(GlobalTrackedData.CODEC).apply(jElement)
        return result.get().left().get().first
    }

    override fun serialize(data: GlobalTrackedData, type: Type, context: JsonSerializationContext): JsonElement {
        val result = JsonOps.INSTANCE.withEncoder(GlobalTrackedData.CODEC).apply(data)
        return result.get().left().get()
    }

    fun bufSerialize(buf: PacketByteBuf, data: GlobalTrackedData) {
        val variant = getVariantStr(data)
        buf.writeString(variant)
        data.encode(buf)
    }

    fun bufDeserialize(buf: PacketByteBuf): GlobalTrackedData {
        val variant = buf.readIdentifier()
        return decoders[variant]?.invoke(buf) ?: throw ReflectiveOperationException("Error invoking decoder for variant $variant")
    }

    fun getVariantStr(data: GlobalTrackedData): String {
        return this.types.entries.find { it.value == data::class }?.key ?: throw IllegalArgumentException("Cannot resolve variant for type ${data::class.qualifiedName}")
    }
}