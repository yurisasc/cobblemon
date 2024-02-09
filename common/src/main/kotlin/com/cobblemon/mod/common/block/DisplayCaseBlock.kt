/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.block.entity.displaycase.DisplayCaseBlockEntity
import com.cobblemon.mod.common.client.render.block.DisplayCaseRenderer
import net.minecraft.block.*
import net.minecraft.block.HorizontalFacingBlock.*
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

@Suppress("OVERRIDE_DEPRECATION")
class DisplayCaseBlock(settings: Settings) : BlockWithEntity(settings) {
    init {
        this.defaultState = this.stateManager.defaultState
            .with(FACING, Direction.NORTH)
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

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
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
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        val entity = world.getBlockEntity(pos) as DisplayCaseBlockEntity
        return entity.updateItem(player, hand)
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        val entity = world.getBlockEntity(pos) as DisplayCaseBlockEntity
        if (!entity.getStack().isEmpty) {
            ItemScatterer.spawn(world, pos, entity.inv)
        }
        super.onBreak(world, pos, state, player)
    }

    override fun getRenderType(state: BlockState?) = BlockRenderType.MODEL

    override fun getComparatorOutput(state: BlockState, world: World, pos: BlockPos): Int {
        val stack = (world.getBlockEntity(pos) as DisplayCaseBlockEntity).getStack()
        val posType = DisplayCaseRenderer.getPositioningType(stack, world)

        if (stack.isEmpty) return 0

        return when (posType) {
            DisplayCaseRenderer.PositioningType.POKE_BALL -> 3
            DisplayCaseRenderer.PositioningType.BLOCK_MODEL -> 2
            DisplayCaseRenderer.PositioningType.ITEM_MODEL -> 1
        }
    }

    override fun hasComparatorOutput(state: BlockState?) = true

    override fun canPathfindThrough(state: BlockState, world: BlockView, pos: BlockPos, type: NavigationType) = false

}