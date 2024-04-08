/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import com.cobblemon.mod.common.CobblemonActivities
import com.cobblemon.mod.common.CobblemonMemories
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.ai.brain.Activity
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer

/**
 * Moves Pokémon in and out of the battling activity.
 *
 * @author Hiroku
 * @since April 8th, 2024
 */
object HandleBattleActivityGoal {
    fun create(): SingleTickTask<PokemonEntity> {
        return TaskTriggerer.task {
            it.group(
                it.queryMemoryOptional(CobblemonMemories.POKEMON_BATTLE),
                it.queryMemoryOptional(MemoryModuleType.WALK_TARGET),
                it.queryMemoryOptional(MemoryModuleType.LOOK_TARGET),
            ).apply(it) { battle, walkTarget, lookTarget ->
                TaskRunnable { world, entity, _ ->
                    val battleUUID = it.getOptionalValue(battle).orElse(null)
                    if (battleUUID != null && !entity.brain.hasActivity(CobblemonActivities.BATTLING_ACTIVITY)) {
                        entity.brain.resetPossibleActivities(listOf(CobblemonActivities.BATTLING_ACTIVITY))
                        entity.brain.forget(MemoryModuleType.WALK_TARGET)
                        entity.brain.forget(MemoryModuleType.LOOK_TARGET)
                        entity.navigation.stop()
                        return@TaskRunnable true
                    } else if (battleUUID == null && entity.brain.hasActivity(CobblemonActivities.BATTLING_ACTIVITY)) {
                        entity.brain.resetPossibleActivities(listOf(Activity.IDLE))
                        entity.brain.forget(MemoryModuleType.WALK_TARGET)
                        entity.brain.forget(MemoryModuleType.LOOK_TARGET)
                        return@TaskRunnable true
                    }
                    return@TaskRunnable false
                }
            }
        }
    }
}