/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.block.entity.DisplayCaseBlockEntity
import com.cobblemon.mod.common.item.PokeBallItem
import com.mojang.serialization.MapCodec
import net.minecraft.block.*
import net.minecraft.block.HorizontalFacingBlock.*
import net.minecraft.core.BlockPos
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.world.entity.player.Player
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.WorldAccess
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.pathfinder.PathComputationType

@Suppress("OVERRIDE_DEPRECATION")
class DisplayCaseBlock(settings: Settings) : BaseEntityBlock(settings) {
    init {
        this.defaultState = this.stateManager.defaultState
            .with(FACING, Direction.NORTH)
            .with(ITEM_DIRECTION, Direction.NORTH)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        var blockState = defaultState
        val worldView = ctx.world
        val blockPos = ctx.blockPos
        ctx.placementDirections.forEach { direction ->
            if (direction.axis.isHorizontal) {
                blockState = blockState
                    .with(FACING, direction)
                    .with(ITEM_DIRECTION, direction)
                        as BlockState
                if (blockState.canPlaceAt(worldView, blockPos)) {
                    return blockState
                }
            }
        }
        return null
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(ITEM_DIRECTION)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): DisplayCaseBlockEntity {
        return DisplayCaseBlockEntity(pos, state)
    }

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

    override fun onUse(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): ActionResult {
        val entity = world.getBlockEntity(pos) as DisplayCaseBlockEntity
        val result = entity.updateItem(player, Hand.MAIN_HAND)
        if ((hit.side != Direction.UP && hit.side != Direction.DOWN) && result == ActionResult.SUCCESS) {
            world.setBlockState(pos, state.with(ITEM_DIRECTION, hit.side.opposite))
        }
        return result
    }

    override fun onBreak(world: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        val entity = world.getBlockEntity(pos) as DisplayCaseBlockEntity
        if (!entity.getStack().isEmpty && !player.isCreative) {
            ItemScatterer.spawn(world, pos, entity.inv)
        }
        return super.onBreak(world, pos, state, player)
    }

    override fun getRenderShape(state: BlockState) = RenderShape.MODEL

    override fun getComparatorOutput(state: BlockState, world: Level, pos: BlockPos): Int {
        val stack = (world.getBlockEntity(pos) as DisplayCaseBlockEntity).getStack()

        if (stack.isEmpty) return 0
        if (stack.item is PokeBallItem) return 3
        if (stack.item is BlockItem) return 2
        return 1
    }

    override fun hasComparatorOutput(state: BlockState?) = true
    override fun getCodec(): MapCodec<out BlockWithEntity> {
        return CODEC
    }

    override fun isPathfindable(state: BlockState, type: PathComputationType): Boolean = false

    companion object {
        val CODEC = createCodec(::DisplayCaseBlock)
        val ITEM_DIRECTION = DirectionProperty.of("item_facing")
    }

}