/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util.adapters.riding

import com.cobblemon.mod.common.api.riding.RidingProperties
import com.cobblemon.mod.common.api.riding.controller.RideControllerDeserializer
import com.cobblemon.mod.common.api.riding.seats.properties.SeatProperties
import com.cobblemon.mod.common.pokemon.riding.CobblemonRidingProperties
import com.cobblemon.mod.common.pokemon.riding.RidingModule
import com.cobblemon.mod.common.util.asIdentifier
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

object RidingPropertiesAdapter : JsonDeserializer<RidingProperties> {

    init {
        RidingModule.configure()
    }

    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): RidingProperties {
        val root = element.asJsonObject
        val seats = root.getAsJsonArray("seats")
        val controllers = root.getAsJsonArray("controllers")
            .map {
                val controller = it as JsonObject
                val key = controller.get("key").asIdentifier

                RideControllerDeserializer.deserialize(key, controller)
            }

        return CobblemonRidingProperties(
            seats.map { context.deserialize(it, SeatProperties::class.java) },
            emptyList(),
            controllers
        )
    }

}