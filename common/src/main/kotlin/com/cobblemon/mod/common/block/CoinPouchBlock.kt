/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import net.minecraft.block.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.WorldAccess

class CoinPouchBlock(settings: Settings) : HorizontalFacingBlock(settings) {

    init {
        this.defaultState = this.stateManager.defaultState
            .with(NATURAL, false)
            .with(FACING, Direction.SOUTH)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        var blockState = defaultState
        val worldView = ctx.world
        val blockPos = ctx.blockPos
        ctx.placementDirections.forEach { direction ->
            if (direction.axis.isHorizontal) {
                blockState = blockState.with(FACING, direction) as BlockState
                if (blockState.canPlaceAt(worldView, blockPos)) {
                    return blockState
                }
            }
        }
        return null
    }

    @Deprecated("Deprecated in Java")
    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return if (direction == state.get(FACING) && !state.canPlaceAt(world, pos)) Blocks.AIR.defaultState
            else super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING, NATURAL)
    }

    companion object {
        val NATURAL: BooleanProperty = BooleanProperty.of("natural")
    }
}