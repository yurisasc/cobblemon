/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.controller

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.riding.RegisterRidingControllerAdapterEvent
import com.google.gson.JsonElement
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object RideControllerDeserializer {

    private val deserializers: MutableMap<Identifier, RideController.Deserializer> = mutableMapOf()

    init {
        CobblemonEvents.REGISTER_RIDING_CONTROLLER_ADAPTER.emit(RegisterRidingControllerAdapterEvent(this.deserializers))
    }

    fun deserialize(key: Identifier, json: JsonElement): RideController {
        val deserializer = this.deserializers[key]
        return deserializer?.deserialize(json) ?: throw IllegalArgumentException("Unknown controller: $key")
    }

    fun decode(buffer: PacketByteBuf): RideController {
        val key = buffer.readIdentifier()
        return this.deserializers[key]?.decode(buffer) ?: throw IllegalArgumentException("Unknown controller: $key")
    }

    fun register(id: Identifier, deserializer: RideController.Deserializer) {
        this.deserializers[id] = deserializer
    }

}