/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import com.cobblemon.mod.common.api.storage.party.PartyStore
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import kotlin.math.abs
import net.minecraft.entity.Entity
import net.minecraft.entity.ai.brain.EntityLookTarget
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.WalkTarget
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer
import net.minecraft.entity.ai.pathing.LandPathNodeMaker
import net.minecraft.entity.ai.pathing.PathNodeType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random

object MoveToOwnerTask {
    fun create(completionRange: Int, maxDistance: Float, teleportDistance: Float): SingleTickTask<PokemonEntity> = TaskTriggerer.task {
        it.group(
            it.queryMemoryOptional(MemoryModuleType.WALK_TARGET),
            it.queryMemoryAbsent(MemoryModuleType.ANGRY_AT)
        ).apply(it) { walkTarget, _ ->
            TaskRunnable { _, entity, _ ->
                val owner = entity.owner ?: return@TaskRunnable false
                if (entity.pokemon.storeCoordinates.get()?.store !is PartyStore) {
                    return@TaskRunnable false
                }
                if (entity.distanceTo(owner) > teleportDistance) {
                    if (tryTeleport(entity, owner)) {
                        entity.brain.forget(MemoryModuleType.LOOK_TARGET)
                        entity.brain.forget(MemoryModuleType.WALK_TARGET)
                    }
                    return@TaskRunnable true
                } else if (entity.distanceTo(owner) > maxDistance && it.getOptionalValue(walkTarget).isEmpty) {
                    entity.brain.remember(MemoryModuleType.LOOK_TARGET, EntityLookTarget(owner, true))
                    entity.brain.remember(MemoryModuleType.WALK_TARGET, WalkTarget(owner, 0.4F, completionRange))
                    return@TaskRunnable true
                }
                return@TaskRunnable false
            }
        }
    }

    private fun tryTeleport(entity: PokemonEntity, owner: Entity): Boolean {
        val blockPos = owner.blockPos
        for (i in 0..9) {
            val j = this.getRandomInt(entity.random, -3, 3)
            val k = this.getRandomInt(entity.random, -1, 1)
            val l = this.getRandomInt(entity.random, -3, 3)
            val succeeded = this.tryTeleportTo(entity, owner, blockPos.x + j, blockPos.y + k, blockPos.z + l)
            if (succeeded) {
                return true
            }
        }
        return false
    }

    private fun tryTeleportTo(entity: PokemonEntity, owner: Entity, x: Int, y: Int, z: Int): Boolean {
        if (abs(x.toDouble() - owner.x) < 2.0 && abs(z - owner.z) < 2.0) {
            return false
        } else if (!this.canTeleportTo(entity, BlockPos(x, y, z))) {
            return false
        } else {
            entity.refreshPositionAndAngles(
                x.toDouble() + 0.5, y.toDouble(), z.toDouble() + 0.5,
                entity.yaw,
                entity.pitch
            )
            entity.navigation.stop()
            return true
        }
    }

    private fun canTeleportTo(entity: PokemonEntity, pos: BlockPos): Boolean {
        val pathNodeType = LandPathNodeMaker.getLandNodeType(entity.world, pos.mutableCopy())
        if (pathNodeType != PathNodeType.WALKABLE) {
            return false
        } else {
            val blockPos = pos.subtract(entity.blockPos)
            return entity.world.isSpaceEmpty(entity, entity.boundingBox.offset(blockPos))
        }
    }

    private fun getRandomInt(random: Random, min: Int, max: Int): Int {
        return random.nextInt(max - min + 1) + min
    }
}