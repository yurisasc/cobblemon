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
import net.minecraft.block.ShapeContext
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView

class EnergyRootBlock(settings: Settings) : Block(settings), Fertilizable {
    companion object {
        private val AABB = VoxelShapes.cuboid(0.2, 0.1, 0.2, 0.8, 1.0, 0.8)
    }

    init {
        this.defaultState = stateManager.defaultState
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        return world.getBlockState(pos.up()).isIn(BlockTags.DIRT) && world.isAir(pos)
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ) = AABB

    //  Maybe turn into Giant Root? idk
    override fun isFertilizable(world: WorldView, pos: BlockPos, state: BlockState, isClient: Boolean) = false
    override fun canGrow(world: World, random: Random, pos: BlockPos, state: BlockState) = true
    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) {}

    override fun getRenderType(state: BlockState) = BlockRenderType.MODEL
}