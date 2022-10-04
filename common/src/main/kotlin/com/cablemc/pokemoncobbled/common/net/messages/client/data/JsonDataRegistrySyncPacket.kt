/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.data

import com.cablemc.pokemoncobbled.common.util.readBigString
import com.cablemc.pokemoncobbled.common.util.writeBigString
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraft.network.PacketByteBuf

abstract class JsonDataRegistrySyncPacket<T>(private val gson: Gson, registryEntries: Collection<T>) : DataRegistrySyncPacket<T>(registryEntries) {

    override fun encodeEntry(buffer: PacketByteBuf, entry: T) {
        val json = this.gson.toJson(entry)
        buffer.writeBigString(json)
    }

    override fun decodeEntry(buffer: PacketByteBuf): T? {
        return try {
            val json = buffer.readBigString()
            this.gson.fromJson(json, this.type().type)
        } catch (e: Exception) {
            null
        }
    }

    abstract fun type(): TypeToken<T>
}