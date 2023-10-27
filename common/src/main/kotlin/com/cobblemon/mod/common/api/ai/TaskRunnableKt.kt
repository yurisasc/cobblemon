/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.ai

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.server.world.ServerWorld

@FunctionalInterface
fun interface TaskRunnableKt<E : LivingEntity> : TaskRunnable<E> {
    operator fun invoke(world: ServerWorld, entity: E): Boolean
    override fun trigger(world: ServerWorld, entity: E, time: Long) = invoke(world, entity)
}