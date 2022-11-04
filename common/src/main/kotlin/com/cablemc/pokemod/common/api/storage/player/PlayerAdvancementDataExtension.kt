/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.storage.player

import com.cablemc.pokemod.common.api.types.ElementalType
import com.google.gson.JsonObject

class PlayerAdvancementDataExtension : PlayerDataExtension {

    var totalCaptureCount: Int = 0
        private set
    var totalEggsHatched: Int = 0
        private set
    var totalEvolvedCount: Int = 0
        private set
    var totalBattleVictoryCount: Int = 0
        private set
    var totalShinyCaptureCount: Int = 0
        private set

    private var totalTypeCaptureCounts = mutableMapOf<ElementalType, Int>()

    override fun name(): String {
        return "advancements"
    }

    override fun serialize(): JsonObject {
        val jObject = JsonObject()
        jObject.addProperty("name", name())
        jObject.addProperty("total_capture_count", totalCaptureCount)
        jObject.addProperty("total_eggs_hatched", totalEggsHatched)
        jObject.addProperty("total_evolve_count", totalEvolvedCount)
        jObject.addProperty("total_battle_victory_count", totalBattleVictoryCount)
        jObject.addProperty("total_shiny_capture_count", totalShinyCaptureCount)
        totalTypeCaptureCounts.forEach {
            val name = it.key.name
            jObject.addProperty("total_" + name + "_capture_count", it.value)
        }
        return jObject
    }

    override fun deserialize(json: JsonObject): PlayerDataExtension {
        val jObject = json.asJsonObject
        val name = jObject.get("name")
        totalCaptureCount = jObject.get("total_capture_count").asInt
        totalEggsHatched = jObject.get("total_eggs_hatched").asInt
        totalEvolvedCount = jObject.get("total_evolve_count").asInt
        totalBattleVictoryCount = jObject.get("total_battle_victory_count").asInt
        totalShinyCaptureCount = jObject.get("total_shiny_capture_count").asInt
        totalTypeCaptureCounts.forEach {
            val typeName = it.key.name
            totalTypeCaptureCounts.replace(it.key, jObject.get("total_" + typeName + "_capture_count").asInt)
        }
        return this
    }

    fun updateTotalCaptureCount() {
        totalCaptureCount++
    }

    fun updateTotalEggsHatched() {
        totalEggsHatched++
    }

    fun updateTotalEvolvedCount() {
        totalEvolvedCount++
    }

    fun updateTotalBattleVictoryCount() {
        totalEvolvedCount++
    }

    fun updateTotalShinyCaptureCount() {
        totalShinyCaptureCount++
    }

    fun getTotalTypeCaptureCount(type: ElementalType): Int {
        if(!totalTypeCaptureCounts.containsKey(key = type)) {
            totalTypeCaptureCounts[type] = 0
        }
        return totalTypeCaptureCounts.get(key = type) ?: 0
    }

    fun updateTotalTypeCaptureCount(type: ElementalType) {
        val count = totalTypeCaptureCounts[type] ?: 0
        if(count == 0)
        {
            totalTypeCaptureCounts[type] = 1
        } else {
            totalTypeCaptureCounts.replace(type, count + 1)
        }
    }
}