/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.util.adapters

import com.cablemc.pokemod.common.api.spawning.condition.TimeRange
import com.cablemc.pokemod.common.util.isInt
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * Adapter for serializing and deserializing [TimeRange]. It deserializes from comma separated time ranges
 * in either name (referencing [TimeRange.Companion.ranges]) or in the format minTick-maxTick.
 *
 * For example, it can deserialize "day,18000-20000" as a [TimeRange]
 *
 * @author Hiroku
 * @since January 26th, 2022
 */
object TimeRangeAdapter : JsonSerializer<TimeRange>, JsonDeserializer<TimeRange> {
    override fun serialize(timeRange: TimeRange, type: Type, ctx: JsonSerializationContext): JsonElement {
        return JsonPrimitive(timeRange.ranges.joinToString { "${it.first}-${it.last}" })
    }

    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): TimeRange {
        val str = json.asString
        val splits = str.split(",")
        if (splits.isEmpty()) {
            return TimeRange()
        }

        val ranges = mutableListOf<IntRange>()
        splits.forEach {
            val range = it.split("-")
            if (range.size == 2 && range[0].isInt() && range[1].isInt()) {
                ranges.add(range[0].toInt()..range[1].toInt())
            } else if (range.size == 1) {
                val matchedRange = TimeRange.ranges.entries.find { it.key.equals(range[0], ignoreCase = true) }?.value
                if (matchedRange != null) {
                    ranges.addAll(matchedRange.ranges)
                }
            }
        }
        return TimeRange(*ranges.toTypedArray())
    }
}