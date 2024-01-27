/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.ai

import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer
import net.minecraft.entity.mob.PathAwareEntity

object FollowPathTask {
    fun create(): SingleTickTask<PathAwareEntity> {
        return TaskTriggerer.task {
            it.group(
                it.queryMemoryValue(MemoryModuleType.PATH)
            ).apply(it) { path ->
                TaskRunnable { world, entity, time ->
                    entity.navigation.startMovingAlong(it.getValue(path), 0.35)
                    return@TaskRunnable true
                }
            }
        }
    }
}