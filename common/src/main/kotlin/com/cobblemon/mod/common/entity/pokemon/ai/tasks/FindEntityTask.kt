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
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.brain.*
import net.minecraft.entity.ai.brain.task.Task
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer
import net.minecraft.server.world.ServerWorld
import java.util.function.Predicate
import net.minecraft.entity.ai.brain.EntityLookTarget
import net.minecraft.entity.ai.brain.LivingTargetCache
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.WalkTarget

/**
 * Must find
 *
 * @author Plastered_Crab
 * @since May 20th, 2024
 */


object FindEntityTask {
    fun <T : LivingEntity?> create(type: EntityType<out T>, maxDistance: Int, targetModule: MemoryModuleType<T>?, speed: Float, completionRange: Int): Task<LivingEntity> {
        return create(type, maxDistance, { entity: LivingEntity? -> true }, { entity: T -> true }, targetModule, speed, completionRange)
    }

    fun <E : LivingEntity?, T : LivingEntity?> create(type: EntityType<out T>, maxDistance: Int, entityPredicate: Predicate<E>, targetPredicate: Predicate<T>, targetModule: MemoryModuleType<T>?, speed: Float, completionRange: Int): Task<E> {
        val i = maxDistance * maxDistance
        val predicate = Predicate { entity: LivingEntity -> type == entity.type && targetPredicate.test(entity as T) }
        return TaskTriggerer.task<E> { context: TaskTriggerer.TaskContext<E> ->
            context.group<MemoryQueryResult<OptionalBox.Mu?, T>, MemoryQueryResult<OptionalBox.Mu?, LookTarget?>, MemoryQueryResult<Const.Mu<Unit?>?, WalkTarget?>, MemoryQueryResult<IdF.Mu?, LivingTargetCache>>(context.queryMemoryOptional<T>(targetModule), context.queryMemoryOptional<LookTarget>(MemoryModuleType.LOOK_TARGET), context.queryMemoryAbsent<WalkTarget>(MemoryModuleType.WALK_TARGET), context.queryMemoryValue<LivingTargetCache>(MemoryModuleType.VISIBLE_MOBS)).apply<TaskRunnable<E>>(context) { targetValue: MemoryQueryResult<OptionalBox.Mu?, T>, lookTarget: MemoryQueryResult<OptionalBox.Mu?, LookTarget?>, walkTarget: MemoryQueryResult<Const.Mu<Unit?>?, WalkTarget?>, visibleMobs: MemoryQueryResult<IdF.Mu?, LivingTargetCache>? ->
                TaskRunnable<E> { world: ServerWorld?, entity: E, time: Long ->
                    val livingTargetCache = context.getValue(visibleMobs) as LivingTargetCache
                    if (entityPredicate.test(entity) && livingTargetCache.anyMatch(predicate)) {
                        val optional = livingTargetCache.findFirst { target: LivingEntity -> target.squaredDistanceTo(entity) <= i.toDouble() && predicate.test(target) }
                        optional.ifPresent { target: LivingEntity?->
                            targetValue.remember(target as? T)
                            lookTarget.remember(EntityLookTarget(target, true))
                            walkTarget.remember(WalkTarget(EntityLookTarget(target, false), speed, completionRange))
                        }
                        return@TaskRunnable true
                    } else {
                        return@TaskRunnable false
                    }
                }
            }
        }
    }
}