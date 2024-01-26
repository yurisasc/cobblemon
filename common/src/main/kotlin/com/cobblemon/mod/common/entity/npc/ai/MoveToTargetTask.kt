/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.npc.ai

import com.cobblemon.mod.common.entity.npc.NPCEntity
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer

object MoveToTargetTask {
    fun create(): SingleTickTask<NPCEntity> {
        return TaskTriggerer.task {
            it.group(
                it.queryMemoryAbsent(MemoryModuleType.PATH),
                it.queryMemoryValue(MemoryModuleType.WALK_TARGET)
            ).apply(it) { path, walkTarget ->
                TaskRunnable { world, entity, time ->
                    val targetVec = it.getValue(walkTarget).lookTarget.blockPos
                    val walkPath = entity.navigation.findPathTo(targetVec, 0)
                    if (walkPath == null) {
                        walkTarget.forget()
                    } else {
                        path.remember(walkPath)
                    }
                    return@TaskRunnable true
                }
            }
        }
    }
}