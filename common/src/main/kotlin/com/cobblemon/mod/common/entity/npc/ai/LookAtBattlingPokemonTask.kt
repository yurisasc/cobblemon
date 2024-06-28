/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.npc.ai

import com.cobblemon.mod.common.CobblemonMemories
import net.minecraft.world.entity.LivingEntity
import net.minecraft.entity.ai.brain.EntityLookTarget
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer

object LookAtBattlingPokemonTask {
    fun create(minDurationTicks: Int = 60, maxDurationTicks: Int = 100): SingleTickTask<LivingEntity> {
        return TaskTriggerer.task {
            it.group(
                it.queryMemoryAbsent(MemoryModuleType.LOOK_TARGET),
                it.queryMemoryValue(CobblemonMemories.BATTLING_POKEMON)
            ).apply(it) { lookTarget, visibleMobs ->
                TaskRunnable { world, _, _ ->
                    val lookEntity = it.getValue(visibleMobs).randomOrNull()?.let(world::getEntity)
                    if (lookEntity == null) {
                        return@TaskRunnable false
                    } else {
                        lookTarget.remember(EntityLookTarget(lookEntity, true), (minDurationTicks..maxDurationTicks).random().toLong())
                        return@TaskRunnable true
                    }
                }
            }
        }
    }
}