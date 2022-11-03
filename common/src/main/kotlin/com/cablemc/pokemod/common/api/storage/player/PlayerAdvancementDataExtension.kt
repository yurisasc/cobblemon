/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.storage.player

import com.google.gson.JsonObject

class PlayerAdvancementDataExtension : PlayerDataExtension {

    private var totalCaptureCount = 0
    private var totalEggsHatched = 0
    private var totalEvolvedCount = 0
    private var totalBattleVictoryCount = 0

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
        return jObject
    }

    override fun deserialize(json: JsonObject): PlayerDataExtension {
        val jObject = json.asJsonObject
        val name = jObject.get("name")
        totalCaptureCount = jObject.get("total_capture_count").asInt
        totalEggsHatched = jObject.get("total_eggs_hatched").asInt
        totalEvolvedCount = jObject.get("total_evolve_count").asInt
        totalBattleVictoryCount = jObject.get("total_battle_victory_count").asInt
        return this
    }

    fun getTotalCaptureCount(): Int {
        return totalCaptureCount
    }

    fun updateTotalCaptureCount() {
        totalCaptureCount++
    }

    fun getTotalEggsHatched(): Int {
        return totalEggsHatched
    }

    fun updateTotalEggsHatched() {
        totalEggsHatched++
    }

    fun getTotalEvolvedCount(): Int {
        return totalEvolvedCount
    }

    fun updateTotalEvolvedCount() {
        totalEvolvedCount++
    }

    fun getTotalBattleVictoryCount(): Int {
        return totalBattleVictoryCount
    }

    fun updateTotalBattleVictoryCount() {
        totalEvolvedCount++
    }
}