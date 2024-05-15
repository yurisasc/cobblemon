/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import net.minecraft.entity.ai.brain.*
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.util.math.intprovider.UniformIntProvider

// modified mojang task for Pokémon species specific behaviour
object WalkTowardsParentSpeciesTask {
    fun create(
        executionRange: UniformIntProvider,
        speed: Float
    ): SingleTickTask<PassiveEntity> = TaskTriggerer.task { context ->
        context.group(
            context.queryMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ADULT),
            context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET),
            context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET)
        ).apply(context) { nearestVisibleAdult , lookTarget, walkTarget ->
            TaskRunnable { _, entity, _ ->
                val passiveEntity = context.getValue(nearestVisibleAdult) as PassiveEntity
                if (
                    entity.isInRange(passiveEntity, (executionRange.max + 1).toDouble())
                    && !entity.isInRange(passiveEntity, executionRange.min.toDouble())
                ) {
                    val walkTargetx = WalkTarget(
                        EntityLookTarget(passiveEntity, false),
                        speed, executionRange.min - 1
                    )

                    lookTarget.remember(
                        EntityLookTarget(
                            passiveEntity,
                            true
                        )
                    )

                    walkTarget.remember(walkTargetx)
                    true
                } else {
                    false
                }
            }
        }
    }
}
