/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.sensor

import com.cobblemon.mod.common.CobblemonMemories
import com.cobblemon.mod.common.entity.npc.NPCEntity
import net.minecraft.entity.ai.brain.sensor.Sensor
import net.minecraft.server.world.ServerWorld

class NPCBattlingSensor : Sensor<NPCEntity>() {
    companion object {
        val OUTPUT_MEMORY_MODULES = setOf(CobblemonMemories.NPC_BATTLING)
    }

    override fun getOutputMemoryModules() = OUTPUT_MEMORY_MODULES
    override fun sense(world: ServerWorld, entity: NPCEntity) {
        val isBattling = entity.isInBattle()
        if (isBattling) {
            entity.getBrain().remember(CobblemonMemories.NPC_BATTLING, true)
        } else if (entity.getBrain().hasMemoryModule(CobblemonMemories.NPC_BATTLING)) {
            entity.getBrain().forget(CobblemonMemories.NPC_BATTLING)
        }
    }
}