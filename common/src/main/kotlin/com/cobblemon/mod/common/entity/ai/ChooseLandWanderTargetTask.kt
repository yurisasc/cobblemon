/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.ai

import com.cobblemon.mod.common.CobblemonMemories
import net.minecraft.entity.ai.FuzzyTargeting
import net.minecraft.entity.ai.brain.BlockPosLookTarget
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.WalkTarget
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer
import net.minecraft.entity.mob.PathAwareEntity

object ChooseLandWanderTargetTask {
    fun create(chance: Int, horizontalRange: Int, verticalRange: Int, walkSpeed: Float, completionRange: Int): SingleTickTask<PathAwareEntity> {
        return TaskTriggerer.task {
            it.group(
                it.queryMemoryAbsent(MemoryModuleType.WALK_TARGET),
                it.queryMemoryOptional(MemoryModuleType.LOOK_TARGET),
                it.queryMemoryAbsent(CobblemonMemories.POKEMON_SLEEPING),
                    ).apply(it) { walkTarget, lookTarget, isSleeping ->
                TaskRunnable { world, entity, time ->
                    if (world.random.nextInt(chance) != 0) return@TaskRunnable false
                    val targetVec = FuzzyTargeting.find(entity, horizontalRange, verticalRange) ?: return@TaskRunnable false
                    walkTarget.remember(WalkTarget(targetVec, walkSpeed, completionRange))
                    lookTarget.remember(BlockPosLookTarget(targetVec.add(0.0, 1.5, 0.0)))
                    return@TaskRunnable true
                }
            }
        }
    }
}
