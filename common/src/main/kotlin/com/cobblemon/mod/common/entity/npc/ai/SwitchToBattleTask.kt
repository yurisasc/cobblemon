/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.npc.ai

import com.cobblemon.mod.common.CobblemonMemories
import com.cobblemon.mod.common.entity.npc.NPCEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer

/**
 * When NPC battling memory is added, this task swaps the NPC's activity over to npc_battling
 *
 * @author Hiroku
 * @since February 24th, 2024
 */
object SwitchToBattleTask {
    fun create(): SingleTickTask<LivingEntity> {
        return TaskTriggerer.task {
            it.group(
                it.queryMemoryOptional(MemoryModuleType.WALK_TARGET),
                it.queryMemoryValue(CobblemonMemories.NPC_BATTLING)
            ).apply(it) { walkTarget, _ ->
                TaskRunnable { _, entity, _ ->
                    walkTarget.forget()
                    entity.brain.doExclusively(NPCEntity.BATTLING)
                    true
                }
            }
        }
    }
}