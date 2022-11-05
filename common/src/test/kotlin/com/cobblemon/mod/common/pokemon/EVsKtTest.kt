/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon

import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

internal class EVsKtTest {
    @Test
    fun `should create a empty set of EVs`() {
        val evs = EVs.createEmpty()
        assertFalse(evs.any { (_, value) -> value > 0 })
    }

    @Test
    fun `attempt to set an illegal amount of evs`() {
        val evs = EVs.createEmpty()
        Stats.mainStats.forEach { stat ->
            evs[stat] = EVs.MAX_STAT_VALUE
        }
        assert(evs.sumOf { it.value } <= EVs.MAX_TOTAL_VALUE)
    }

    @Test
    fun `attempt to add an illegal amount of evs`() {
        val evs = EVs.createEmpty()
        evs[Stats.HP] = EVs.MAX_STAT_VALUE
        evs[Stats.ATTACK] = EVs.MAX_STAT_VALUE
        val added = evs.add(Stats.DEFENCE, 7)
        assert(added == 6)
    }

    @Test
    fun `default value must be 0`() {
        val evs = EVs()
        assert(evs.getOrDefault(Stats.HP) == 0)
    }

    @Test
    fun `nbt loading`() {
        val evs = EVs.createEmpty()
        val nbt = NbtCompound().apply {
            putInt(Stats.HP.id, 2)
        }
        evs.loadFromNBT(nbt)
        assert(evs[Stats.HP] == 2)
    }

    @Test
    fun `nbt writing`() {
        val evs = EVs.createEmpty()
        evs[Stats.HP] = 1
        assert(evs.saveToNBT(NbtCompound()).getInt(Stats.HP.id) == 1)
    }

    @Test
    fun `json loading`() {
        val evs = EVs.createEmpty()
        val json = JsonObject().apply {
            addProperty(Stats.HP.id, 2)
        }
        evs.loadFromJSON(json)
        assert(evs[Stats.HP] == 2)
    }

    @Test
    fun `json writing`() {
        val evs = EVs.createEmpty()
        evs[Stats.HP] = 1
        assert(evs.saveToJSON(JsonObject()).get(Stats.HP.id).asInt == 1)
    }
}