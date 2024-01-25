/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.controller.properties

import com.cobblemon.mod.common.util.asIdentifier
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object RideControllerPropertyDeserializer {

    fun deserialize(key: Identifier, json: JsonElement): RideControllerProperties {
        val data = json.asJsonObject
        val controller = data.get("controller").asIdentifier

        return RideControllerProperties.deserializers[controller]?.deserialize(
            data.getAsJsonObject("properties")
        ) ?: throw JsonParseException("Invalid capability: $controller")
    }

}

interface Deserializer<T : RideControllerProperties> {

    fun deserialize(json: JsonElement): T

    fun decode(buffer: PacketByteBuf): T

}