/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.google.common.collect.ImmutableMap
import net.minecraft.block.CropBlock
import net.minecraft.entity.ai.brain.BlockPosLookTarget
import net.minecraft.entity.ai.brain.MemoryModuleState
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.WalkTarget
import net.minecraft.entity.ai.brain.task.MultiTickTask
import net.minecraft.item.BoneMealItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import java.util.*

//todo(broccoli): add a cooldown
class FertilizerTask : MultiTickTask<PokemonEntity>(
        ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT,
                MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT
        )
) {

    companion object {
        private const val MAX_DURATION = 80
    }

    private var startTime: Long = 0
    private var lastEndEntityAge: Long = 0
    private var duration = 0
    private var pos = Optional.empty<BlockPos>()

    override fun shouldRun(world: ServerWorld, entity: PokemonEntity): Boolean {
        this.pos = findBoneMealPos(world, entity)
        return pos.isPresent
    }

    override fun shouldKeepRunning(world: ServerWorld, entity: PokemonEntity, time: Long): Boolean {
        return duration < MAX_DURATION && pos.isPresent
    }

    private fun findBoneMealPos(world: ServerWorld, entity: PokemonEntity): Optional<BlockPos> {
        val mutable = BlockPos.Mutable()
        var optional = Optional.empty<BlockPos>()
        var i = 0

        //todo(broccoli): cleanup
        for (j in -1..1) {
            for (k in -1..1) {
                for (l in -1..1) {
                    mutable[entity.blockPos, j, k] = l
                    if (canBoneMeal(mutable, world)) {
                        ++i
                        if (world.random.nextInt(i) == 0) {
                            optional = Optional.of(mutable.toImmutable())
                        }
                    }
                }
            }
        }

        return optional
    }

    private fun canBoneMeal(pos: BlockPos, world: ServerWorld): Boolean {
        val blockState = world.getBlockState(pos)
        val block = blockState.block

        return block is CropBlock && !block.isMature(blockState)
    }

    override fun run(world: ServerWorld, entity: PokemonEntity, time: Long) {
        addLookWalkTargets(entity)
        startTime = time
        duration = 0
    }

    private fun addLookWalkTargets(entity: PokemonEntity) {
        pos.ifPresent { pos: BlockPos? ->
            val blockPosLookTarget = BlockPosLookTarget(pos)
            entity.brain.remember(
                    MemoryModuleType.LOOK_TARGET,
                    blockPosLookTarget
            )
            entity.brain.remember(
                    MemoryModuleType.WALK_TARGET,
                    WalkTarget(blockPosLookTarget, 0.5f, 1)
            )
        }
    }

    override fun finishRunning(world: ServerWorld, entity: PokemonEntity, time: Long) {
        lastEndEntityAge = entity.age.toLong()
    }

    override fun keepRunning(world: ServerWorld, entity: PokemonEntity, time: Long) {
        val blockPos = pos.get()

        if (time >= this.startTime && blockPos.isWithinDistance(entity.pos, 1.0)) {
            if (BoneMealItem.useOnFertilizable(createBoneMealStack(), world, blockPos)) {
                world.syncWorldEvent(1505, blockPos, 0)
                pos = this.findBoneMealPos(world, entity)
                addLookWalkTargets(entity)
                startTime = time + 40L
            }

            ++duration
        }
    }

    private fun createBoneMealStack() = ItemStack(Items.BONE_MEAL, 1)
}