/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util.adapters.riding

import com.cobblemon.mod.common.api.riding.RidingProperties
import com.cobblemon.mod.common.api.riding.capability.RidingCapability
import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerPropertyDeserializer
import com.cobblemon.mod.common.api.riding.seats.properties.SeatProperties
import com.cobblemon.mod.common.pokemon.riding.CobblemonRidingProperties
import com.cobblemon.mod.common.pokemon.riding.RidingModule
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

object RidingPropertiesAdapter : JsonDeserializer<RidingProperties> {

    init {
        RidingModule.configure()
    }

    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): RidingProperties {
        val root = element.asJsonObject
        val seats = root.getAsJsonArray("seats")
        val capabilities = root.getAsJsonObject("capabilities")
            .asMap()
            .map {
                val key = cobblemonResource(it.key)
                val data: JsonElement = it.value

                val properties = RideControllerPropertyDeserializer.deserialize(key, data)
                RidingCapability.create(key, properties)
            }


        return CobblemonRidingProperties(
            seats.map { context.deserialize(it, SeatProperties::class.java) },
            emptyList(),
            capabilities
        )
    }

}