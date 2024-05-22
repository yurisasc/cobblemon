/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import com.mojang.datafixers.kinds.Const
import com.mojang.datafixers.kinds.IdF
import com.mojang.datafixers.kinds.OptionalBox
import com.mojang.datafixers.util.Unit
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.brain.*
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer
import net.minecraft.entity.mob.MobEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.MathHelper
import net.minecraft.entity.ai.brain.EntityLookTarget
import net.minecraft.entity.ai.brain.LivingTargetCache
import net.minecraft.entity.ai.brain.MemoryModuleType


/**
 * Must kill
 *
 * @author Plastered_Crab
 * @since May 20th, 2024
 */


object AttackTask {
    fun create(distance: Int, forwardMovement: Float): SingleTickTask<MobEntity> {
        return TaskTriggerer.task { context: TaskTriggerer.TaskContext<MobEntity> ->
            context.group(context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryValue(MemoryModuleType.ATTACK_TARGET), context.queryMemoryValue(MemoryModuleType.VISIBLE_MOBS)).apply(context) { walkTarget: MemoryQueryResult<Const.Mu<Unit?>?, WalkTarget?>?, lookTarget: MemoryQueryResult<OptionalBox.Mu?, LookTarget?>, attackTarget: MemoryQueryResult<IdF.Mu?, LivingEntity>?, visibleMobs: MemoryQueryResult<IdF.Mu?, LivingTargetCache>? ->
                TaskRunnable { world: ServerWorld?, entity: MobEntity, time: Long ->
                    val livingEntity = context.getValue(attackTarget) as LivingEntity
                    if (livingEntity.isInRange(entity, distance.toDouble()) && (context.getValue(visibleMobs) as LivingTargetCache).contains(livingEntity)) {
                        lookTarget.remember(EntityLookTarget(livingEntity, true))
                        entity.moveControl.strafeTo(-forwardMovement, 0.0f)
                        entity.yaw = MathHelper.clampAngle(entity.yaw, entity.headYaw, 0.0f)
                        return@TaskRunnable true
                    } else {
                        return@TaskRunnable false
                    }
                }
            }
        }
    }
}