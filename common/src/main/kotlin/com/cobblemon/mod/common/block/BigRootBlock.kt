/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.world.BigRootPropagatedEvent
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Fertilizable
import net.minecraft.block.ShapeContext
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView

class BigRootBlock(settings: Settings) : Block(settings), Fertilizable {
    companion object {
        const val MAX_PROPAGATING_LIGHT_LEVEL = 11
        private val AABB = VoxelShapes.cuboid(0.2, 0.3, 0.2, 0.8, 1.0, 0.8)
    }

    init {
        this.defaultState = stateManager.defaultState
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ) = AABB

    override fun hasRandomTicks(state: BlockState) = true
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        // Check for propagation
        if (random.nextDouble() < Cobblemon.config.bigRootPropagationChance && world.getLightLevel(pos) < MAX_PROPAGATING_LIGHT_LEVEL) {
            spreadFrom(world, pos, random)
        }
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        return world.getBlockState(pos.up()).isIn(BlockTags.DIRT) && world.isAir(pos)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return if (!world.getBlockState(pos.up()).isIn(BlockTags.DIRT)) {
            Blocks.AIR.defaultState
        } else {
            super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
        }
    }

    fun spreadFrom(world: ServerWorld, pos: BlockPos, random: Random) {
        for (xDiff in -1..1) {
            for (zDiff in -1..1) {
                if (xDiff == 0 && zDiff == 0) {
                    continue
                }

                val adjacent = pos.add(xDiff, 0, zDiff)
                if (canPlaceAt(world.getBlockState(adjacent), world, adjacent)) {
                    val isEnergyRoot = random.nextFloat() < Cobblemon.config.energyRootChance
                    val event = BigRootPropagatedEvent(
                        world = world,
                        pos = pos,
                        newRootPosition = adjacent,
                        isEnergyRoot = isEnergyRoot
                    )

                    CobblemonEvents.BIG_ROOT_PROPAGATED.postThen(
                        event = event,
                        ifCanceled = {},
                        ifSucceeded = { ev ->
                            world.setBlockState(
                                ev.newRootPosition,
                                if (ev.isEnergyRoot) CobblemonBlocks.ENERGY_ROOT.defaultState else defaultState
                            )
                        }
                    )

                    return
                }
            }
        }
    }

    override fun isFertilizable(world: WorldView, pos: BlockPos, state: BlockState, isClient: Boolean) = true
    override fun canGrow(world: World, random: Random, pos: BlockPos, state: BlockState) = true
    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) {
        spreadFrom(world, pos, random)
    }

    override fun getRenderType(state: BlockState) = BlockRenderType.MODEL
}