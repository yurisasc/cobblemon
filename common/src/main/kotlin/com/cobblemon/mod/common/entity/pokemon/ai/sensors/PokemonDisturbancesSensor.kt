/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.ai

import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.sensor.Sensor
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity

class PokemonDisturbancesSensor : Sensor<PokemonEntity>() {
    override fun sense(world: ServerWorld, entity: PokemonEntity) {
        val nearestPlayers = world.players
                .filter { it.isAlive && entity.squaredDistanceTo(it) <= 16 * 16 }
                .minByOrNull { entity.squaredDistanceTo(it) }

        nearestPlayers?.let {
            entity.brain.remember(MemoryModuleType.DISTURBANCE_LOCATION, BlockPos(it.blockPos))
        }
    }

    override fun getOutputMemoryModules(): Set<MemoryModuleType<*>> {
        return setOf(MemoryModuleType.DISTURBANCE_LOCATION)
    }
}
