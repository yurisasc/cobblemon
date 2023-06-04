/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.Fertilizable
import net.minecraft.item.ItemPlacementContext
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldView

class EnergyRootBlock(settings: Settings) : Block(settings), Fertilizable {
    init {
        this.defaultState = stateManager.defaultState
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
    }

    override fun hasRandomTicks(state: BlockState) = true
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        // Check for propagation
        if (random.nextDouble() < 0.01 && world.getLightLevel(pos) < 12) {
            spreadFrom(world, pos, random)
        }
    }

    override fun getPlacementState(context: ItemPlacementContext): BlockState? {
        return defaultState/*.with(HorizontalFacingBlock.FACING, context.horizontalPlayerFacing)*/
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val canPlace = world.getBlockState(pos.up()).isIn(BlockTags.DIRT) && world.isAir(pos)
        return canPlace
    }

    fun getPropagatingBlockState(random: Random): BlockState {
        return if (random.nextFloat() < 0.03) {
            // TODO big root instead
            defaultState/*.with(HorizontalFacingBlock.FACING, Direction.fromHorizontal(random.nextInt(4)))*/
        } else {
            defaultState/*.with(HorizontalFacingBlock.FACING, Direction.fromHorizontal(random.nextInt(4)))*/
        }
    }

    fun spreadFrom(world: ServerWorld, pos: BlockPos, random: Random) {
        for (xDiff in -1..1) {
            for (zDiff in -1..1) {
                if (xDiff == 0 && zDiff == 0) {
                    continue
                }

                for (yDiff in -1..1) {
                    val adjacent = pos.add(xDiff, yDiff, zDiff)
                    if (canPlaceAt(world.getBlockState(adjacent), world, adjacent)) {
                        world.setBlockState(adjacent, getPropagatingBlockState(random), NOTIFY_LISTENERS)
                        return
                    }
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