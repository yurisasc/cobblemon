/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import kotlin.math.min
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Fertilizable
import net.minecraft.block.PlantBlock
import net.minecraft.block.ShapeContext
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.IntProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView

class RevivalHerbBlock(settings: Settings) : PlantBlock(settings), Fertilizable {
    companion object {
        const val MIN_AGE = 0
        const val MAX_AGE = 8
        val AGE = IntProperty.of("age", MIN_AGE, MAX_AGE)
        val IS_WILD = BooleanProperty.of("is_wild")

        val STAGE_0_AABB = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.2, 1.0)
        val STAGE_1_AABB = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.4, 1.0)
        val STAGE_2_AABB = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.6, 1.0)
        val STAGE_3_AABB = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.8, 1.0)
    }

    init {
        defaultState = this.stateManager.defaultState.with(AGE, MIN_AGE).with(IS_WILD, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(AGE)
        builder.add(IS_WILD)
    }

    override fun canGrow(world: World, random: Random, pos: BlockPos, state: BlockState): Boolean {
        return state.get(AGE) < MAX_AGE
    }

    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) {
        world.setBlockState(pos, state.with(AGE, min(state.get(AGE) + 1, MAX_AGE)), NOTIFY_LISTENERS)
    }

    override fun isFertilizable(world: WorldView, pos: BlockPos, state: BlockState, isClient: Boolean): Boolean {
        return state.get(AGE) < MAX_AGE
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val down = world.getBlockState(pos.down())
        val existing = world.getBlockState(pos)
        return if (existing.isAir || existing.block == Blocks.MOSS_CARPET) {
            (down.isIn(BlockTags.DIRT) && state.get(IS_WILD)) || down.block == Blocks.FARMLAND
        } else {
            false
        }
    }

    override fun hasRandomTicks(state: BlockState) = state.get(AGE) < MAX_AGE

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ) = when (state.get(AGE)) {
        1, 2 -> STAGE_0_AABB
        3, 4, 5 -> STAGE_1_AABB
        6, 7, 8 -> STAGE_2_AABB
        else -> STAGE_3_AABB
    }
}