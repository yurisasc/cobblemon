/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.fossilmachine

import com.cobblemon.mod.common.block.entity.fossil.FossilMultiblockEntity
import com.cobblemon.mod.common.api.multiblock.MultiblockBlock
import com.cobblemon.mod.common.api.multiblock.MultiblockEntity
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockBuilder
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class FossilMonitorBlock(properties: Settings) : MultiblockBlock(properties) {
    init {
        defaultState = defaultState.with(HorizontalFacingBlock.FACING, Direction.NORTH)
    }

    override fun createMultiBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): FossilMultiblockEntity {
        return FossilMultiblockEntity(
            pos, state, FossilMultiblockBuilder(pos)
        )
    }

    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState? {
        return defaultState.with(HorizontalFacingBlock.FACING, blockPlaceContext.horizontalPlayerFacing)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(HorizontalFacingBlock.FACING)
        builder.add(PROGRESS)
    }

    @Deprecated("Deprecated in Java")
    override fun hasComparatorOutput(state: BlockState?): Boolean {
        // TODO: return false if not attached to a multiblock structure
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun getComparatorOutput(state: BlockState, world: World?, pos: BlockPos?): Int {
        if(world == null || pos == null) {
            return 0
        }
        val monitorEntity = world.getBlockEntity(pos) as MultiblockEntity
        val multiBlockEntity = monitorEntity.multiblockStructure
        if(multiBlockEntity != null) {
            return multiBlockEntity.getComparatorOutput(state, world, pos)
        }
        return 0
    }

    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        return HITBOX
    }

    companion object {
        //0 is off
        val PROGRESS = IntProperty.of("progress", 0, 9)
        val HITBOX = VoxelShapes.union(
            VoxelShapes.cuboid(0.0625, 0.0, 0.0625, 0.9375, 0.375, 0.9375),
            VoxelShapes.cuboid(0.0625, 0.875, 0.0625, 0.9375, 1.0, 0.9375),
            VoxelShapes.cuboid(0.8125, 0.375, 0.0625, 0.9375, 0.875, 0.9375),
            VoxelShapes.cuboid(0.1875, 0.375, 0.125, 0.8125, 0.875, 0.9375),
            VoxelShapes.cuboid(0.0625, 0.375, 0.0625, 0.1875, 0.875, 0.9375)
        )
    }
}
