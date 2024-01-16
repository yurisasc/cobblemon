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
import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerProperties
import com.cobblemon.mod.common.util.asCobblemonIdentifier
import com.cobblemon.mod.common.util.asIdentifier
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import net.minecraft.util.Identifier

object RideControllerPropertyDeserializer {

    private val mappings: Map<Identifier, Deserializer<*>>

    init {
        val generator: MutableMap<Identifier, Deserializer<*>> = mutableMapOf()
        CobblemonEvents.REGISTER_RIDING_CONTROLLER_ADAPTER.post(RegisterRidingControllerAdapterEvent(generator))

        this.mappings = generator
    }

    fun deserialize(key: Identifier, json: JsonElement): RideControllerProperties {
        val data = json.asJsonObject
        val controller = data.get("controller").asIdentifier

        return mappings[controller]?.deserialize(
            data.getAsJsonObject("properties")
        ) ?: throw JsonParseException("Invalid capability: $controller")
    }

}

interface Deserializer<T : RideControllerProperties> {

    fun deserialize(json: JsonElement): T

}