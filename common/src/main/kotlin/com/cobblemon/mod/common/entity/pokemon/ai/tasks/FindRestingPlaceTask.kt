/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import com.cobblemon.mod.common.CobblemonMemories
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.api.storage.party.PartyStore
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.getBlockPositions
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.WalkTarget
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer
import net.minecraft.util.math.Box

object FindRestingPlaceTask {
    fun create(horizontalSearchDistance: Int, verticalSearchDistance: Int): SingleTickTask<PokemonEntity> {
        return TaskTriggerer.task {
            it.group(
                it.queryMemoryAbsent(MemoryModuleType.ANGRY_AT),
                it.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET),
                it.queryMemoryAbsent(MemoryModuleType.WALK_TARGET),
                it.queryMemoryAbsent(CobblemonMemories.POKEMON_BATTLE),
                it.queryMemoryValue(CobblemonMemories.POKEMON_DROWSY),
                it.queryMemoryAbsent(CobblemonMemories.REST_PATH_COOLDOWN)
            ).apply(it) { _, _, walkTarget, _, pokemonDrowsy, restPathCooldown ->
                TaskRunnable { world, entity, _ ->
                    return@TaskRunnable if (it.getValue(pokemonDrowsy) && entity.pokemon.status?.status != Statuses.SLEEP && entity.pokemon.storeCoordinates.get()?.store !is PartyStore) {
                        entity.brain.remember(CobblemonMemories.REST_PATH_COOLDOWN, true, 40)
                        val position = entity.world
                            .getBlockPositions(Box.of(entity.pos, horizontalSearchDistance.toDouble(), verticalSearchDistance.toDouble(), horizontalSearchDistance.toDouble()))
                            .filter(entity::canSleepAt)
//                            .randomOrNull()
                            .minByOrNull { it.getSquaredDistance(entity.pos) }
                        if (position != null) {
                            walkTarget.remember(WalkTarget(position.down(), 0.3F, 1))
                        }
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }
}