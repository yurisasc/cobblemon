/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.npc.ai

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer
import net.minecraft.util.Unit

object MeleeAttackTask {
    fun create(range: Float, cooldownTicks: Long): SingleTickTask<LivingEntity> = TaskTriggerer.task {
        it.group(
            it.queryMemoryValue(MemoryModuleType.ATTACK_TARGET),
            it.queryMemoryOptional(MemoryModuleType.ATTACK_COOLING_DOWN)
        ).apply(it) { attackTarget, cooldown ->
            TaskRunnable { world, entity, _ ->
                val attackTarget = it.getValue(attackTarget)
                if (entity.distanceTo(attackTarget) <= range) {
                    entity.tryAttack(attackTarget)
                    cooldown.remember(true, cooldownTicks)
                    true
                } else {
                    false
                }
            }
        }
    }
}