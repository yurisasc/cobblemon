/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.gui.TMMScreenHandler
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.net.messages.client.ui.OpenTMMPacket
import net.minecraft.block.*
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class TMBlock(properties: Settings): HorizontalFacingBlock(properties), Waterloggable {
    companion object {
        val WATERLOGGED = BooleanProperty.of("waterlogged")
        val ON = BooleanProperty.of("on")

        private var NORTH_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.3125, 0.9375),
            VoxelShapes.cuboid(0.0, 0.3125, 0.75, 1.0, 0.9375, 0.9375),
            VoxelShapes.cuboid(0.0625, 0.3125, 0.0625, 0.9375, 0.875, 0.9375)
        )

        private var SOUTH_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0, 0.0, 0.0625, 1.0, 0.3125, 1.0),
            VoxelShapes.cuboid(0.0, 0.3125, 0.0625, 1.0, 0.9375, 0.25),
            VoxelShapes.cuboid(0.0625, 0.3125, 0.0625, 0.9375, 0.875, 0.9375)
        )

        private var WEST_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0, 0.0, 0.0, 0.9375, 0.3125, 1.0),
            VoxelShapes.cuboid(0.75, 0.3125, 0.0, 0.9375, 0.9375, 1.0),
            VoxelShapes.cuboid(0.0625, 0.3125, 0.0625, 0.9375, 0.875, 0.9375)
        )

        private var EAST_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0625, 0.0, 0.0, 1.0, 0.3125, 1.0),
            VoxelShapes.cuboid(0.0625, 0.3125, 0.0, 0.25, 0.9375, 1.0),
            VoxelShapes.cuboid(0.0625, 0.3125, 0.0625, 0.9375, 0.875, 0.9375)
        )

    }

    init {
        defaultState = this.stateManager.defaultState.with(FACING, Direction.NORTH)
            .with(WATERLOGGED, false)
            .with(ON, false)
    }

    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState? {
        val abovePosition = blockPlaceContext.blockPos.up()
        val world = blockPlaceContext.world
        if (world.getBlockState(abovePosition).canReplace(blockPlaceContext) && !world.isOutOfHeightLimit(abovePosition)) {
            return defaultState
                .with(FACING, blockPlaceContext.horizontalPlayerFacing)
                .with(WATERLOGGED, blockPlaceContext.world.getFluidState(blockPlaceContext.blockPos).fluid == Fluids.WATER)
                .with(ON, false)
        }

        return null
    }

    @Deprecated("Deprecated in Java")
    override fun canPathfindThrough(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, pathComputationType: NavigationType) = false

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(WATERLOGGED)
        builder.add(ON)
    }

    @Deprecated("Deprecated in Java")
    override fun rotate(blockState: BlockState, rotation: BlockRotation) =
        blockState.with(FACING, rotation.rotate(blockState.get(FACING)))

    @Deprecated("Deprecated in Java")
    override fun mirror(blockState: BlockState, mirror: BlockMirror): BlockState {
        return blockState.rotate(mirror.getRotation(blockState.get(FACING)))
    }

    @Deprecated("Deprecated in Java")
    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos?, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block)) super.onStateReplaced(state, world, pos, newState, moved)
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(
        blockState: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        interactionHand: Hand,
        blockHitResult: BlockHitResult
    ): ActionResult {
        if (world.isClient) {
            return ActionResult.SUCCESS
        }
        player.playSound(CobblemonSounds.TMM_ON, SoundCategory.BLOCKS, 1.0f, 1.0f)
        val serverPlayer = player as ServerPlayerEntity
        serverPlayer.openHandledScreen(blockState.createScreenHandlerFactory(world, pos))
        return ActionResult.SUCCESS
    }

    override fun createScreenHandlerFactory(
        state: BlockState?,
        world: World?,
        pos: BlockPos?
    ): NamedScreenHandlerFactory {
        return SimpleNamedScreenHandlerFactory(::TMMScreenHandler, Text.of("TM Machine"))
    }

    @Deprecated("Deprecated in Java")
    override fun getRenderType(blockState: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun getFluidState(state: BlockState): FluidState? {
        return if (state.get(WATERLOGGED)) {
            Fluids.WATER.getStill(false)
        } else super.getFluidState(state)
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return when (state.get(FACING)) {
            Direction.NORTH -> NORTH_OUTLINE
            Direction.SOUTH -> SOUTH_OUTLINE
            Direction.WEST -> WEST_OUTLINE
            else -> EAST_OUTLINE
        }
    }
}