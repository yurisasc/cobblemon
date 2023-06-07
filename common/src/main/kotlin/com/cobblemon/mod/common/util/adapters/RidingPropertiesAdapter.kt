/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util.adapters

import com.cobblemon.mod.common.api.riding.RidingProperties
import com.cobblemon.mod.common.api.riding.seats.properties.SeatProperties
import com.cobblemon.mod.common.api.riding.properties.mounting.CobblemonMountingTypes
import com.cobblemon.mod.common.pokemon.riding.CobblemonMountingProperties
import com.cobblemon.mod.common.pokemon.riding.CobblemonRidingProperties
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

object RidingPropertiesAdapter : JsonDeserializer<RidingProperties>, JsonSerializer<RidingProperties> {

    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): RidingProperties {
        val root = element.asJsonObject
        val seats = root.getAsJsonArray("seats")

        return CobblemonRidingProperties(
            seats.map { context.deserialize(it, SeatProperties::class.java) },
            listOf(),
            mapOf(CobblemonMountingTypes.LAND to CobblemonMountingProperties())
        )
    }

    override fun serialize(properties: RidingProperties, type: Type, context: JsonSerializationContext): JsonElement {
        val content = JsonObject()
        content.add("seats", writeArray(properties.seats().map { context.serialize(it) }))

        return content
    }

    private fun writeArray(elements: List<JsonElement>): JsonArray {
        val array = JsonArray()
        elements.forEach { array.add(it) }

        return array
    }

}