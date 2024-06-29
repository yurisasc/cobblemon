/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.storage.pc.link.PCLinkManager
import com.cobblemon.mod.common.api.storage.pc.link.ProximityPCLink
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.block.entity.PCBlockEntity
import com.cobblemon.mod.common.net.messages.client.storage.pc.OpenPCPacket
import com.cobblemon.mod.common.util.isInBattle
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.playSoundServer
import com.cobblemon.mod.common.util.toVec3d
import net.minecraft.world.level.block.Block
import net.minecraft.block.Blocks
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.block.ShapeContext
import net.minecraft.block.Waterloggable
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.world.item.ItemPlacementContext
import net.minecraft.server.level.ServerPlayer
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.world.InteractionResult
import net.minecraft.util.Mirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.StringRepresentable
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.InteractionResult
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldEvents
import net.minecraft.world.level.LevelReader
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class PCBlock(properties: Properties): BaseEntityBlock(properties), SimpleWaterloggedBlock {
    companion object {
        val CODEC = codec(::PCBlock)
        val PART = EnumProperty.create("part", PCPart::class.java)
        val ON = BooleanProperty.create("on")
        val WATERLOGGED = BooleanProperty.create("waterlogged")

        private val NORTH_AABB_TOP = Shapes.or(
            Shapes.box(0.1875, 0.0, 0.0, 0.8125, 0.875, 0.125),
            Shapes.box(0.125, 0.8125, 0.125, 0.875, 0.9375, 0.6875),
            Shapes.box(0.125, 0.125, 0.125, 0.875, 0.8125, 0.625),
            Shapes.box(0.0625, 0.0, 0.125, 0.125, 0.9375, 0.6875),
            Shapes.box(0.125, 0.0, 0.125, 0.875, 0.125, 0.6875),
            Shapes.box(0.875, 0.0, 0.125, 0.9375, 0.9375, 0.6875),
            Shapes.box(0.125, 0.0, 0.6875, 0.875, 0.0625, 0.875)
        )

        private val SOUTH_AABB_TOP = Shapes.or(
            Shapes.box(0.1875, 0.0, 0.875, 0.8125, 0.875, 1.0),
            Shapes.box(0.125, 0.8125, 0.3125, 0.875, 0.9375, 0.875),
            Shapes.box(0.125, 0.125, 0.375, 0.875, 0.8125, 0.875),
            Shapes.box(0.875, 0.0, 0.3125, 0.9375, 0.9375, 0.875),
            Shapes.box(0.125, 0.0, 0.3125, 0.875, 0.125, 0.875),
            Shapes.box(0.0625, 0.0, 0.3125, 0.125, 0.9375, 0.875),
            Shapes.box(0.125, 0.0, 0.125, 0.875, 0.0625, 0.3125)
        )

        private val WEST_AABB_TOP = Shapes.or(
            Shapes.box(0.0, 0.0, 0.1875, 0.125, 0.875, 0.8125),
            Shapes.box(0.125, 0.8125, 0.125, 0.6875, 0.9375, 0.875),
            Shapes.box(0.125, 0.125, 0.125, 0.625, 0.8125, 0.875),
            Shapes.box(0.125, 0.0, 0.0625, 0.6875, 0.9375, 0.125),
            Shapes.box(0.125, 0.0, 0.125, 0.6875, 0.125, 0.875),
            Shapes.box(0.125, 0.0, 0.875, 0.6875, 0.9375, 0.9375),
            Shapes.box(0.6875, 0.0, 0.125, 0.875, 0.0625, 0.875)
        )

        private val EAST_AABB_TOP = Shapes.or(
            Shapes.box(0.875, 0.0, 0.1875, 1.0, 0.875, 0.8125),
            Shapes.box(0.3125, 0.8125, 0.125, 0.875, 0.9375, 0.875),
            Shapes.box(0.375, 0.125, 0.125, 0.875, 0.8125, 0.875),
            Shapes.box(0.3125, 0.0, 0.0625, 0.875, 0.9375, 0.125),
            Shapes.box(0.3125, 0.0, 0.125, 0.875, 0.125, 0.875),
            Shapes.box(0.3125, 0.0, 0.875, 0.875, 0.9375, 0.9375),
            Shapes.box(0.125, 0.0, 0.125, 0.3125, 0.0625, 0.875)
        )

        private val NORTH_AABB_BOTTOM = Shapes.or(
            Shapes.box(0.0625, 0.0625, 0.125, 0.9375, 1.0, 0.9375),
            Shapes.box(0.125, 0.0, 0.125, 0.875, 0.0625, 0.875),
            Shapes.box(0.1875, 0.0, 0.0, 0.8125, 1.0, 0.125)
        )

        private val SOUTH_AABB_BOTTOM = Shapes.or(
            Shapes.box(0.0625, 0.0625, 0.0625, 0.9375, 1.0, 0.875),
            Shapes.box(0.125, 0.0, 0.125, 0.875, 0.0625, 0.875),
            Shapes.box(0.1875, 0.0, 0.875, 0.8125, 1.0, 1.0)
        )

        private val WEST_AABB_BOTTOM = Shapes.or(
            Shapes.box(0.125, 0.0625, 0.0625, 0.9375, 1.0, 0.9375),
            Shapes.box(0.125, 0.0, 0.125, 0.875, 0.0625, 0.875),
            Shapes.box(0.0, 0.0, 0.1875, 0.125, 1.0, 0.8125)
        )

        private val EAST_AABB_BOTTOM = Shapes.or(
            Shapes.box(0.0625, 0.0625, 0.0625, 0.875, 1.0, 0.9375),
            Shapes.box(0.125, 0.0, 0.125, 0.875, 0.0625, 0.875),
            Shapes.box(0.875, 0.0, 0.1875, 1.0, 1.0, 0.8125)
        )
    }

    enum class PCPart(private val label: String) : StringRepresentable {
        TOP("top"),
        BOTTOM("bottom");
        override fun getSerializedName(): String = label
    }

    init {
        registerDefaultState(stateDefinition.any()
            .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
            .setValue(PART, PCPart.BOTTOM)
            .setValue(ON, false)
            .setValue(WATERLOGGED, false))
    }

    override fun createBlockEntity(blockPos: BlockPos, blockState: BlockState) = if (blockState.getValue(PART) == PCPart.BOTTOM) PCBlockEntity(blockPos, blockState) else null

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape {
        return if (blockState.getValue(PART) == PCPart.TOP)  {
            when (blockState.getValue(HorizontalDirectionalBlock.FACING)) {
                Direction.SOUTH -> SOUTH_AABB_TOP
                Direction.WEST -> WEST_AABB_TOP
                Direction.EAST -> EAST_AABB_TOP
                else -> NORTH_AABB_TOP
            }
        } else {
            when (blockState.getValue(HorizontalDirectionalBlock.FACING)) {
                Direction.SOUTH -> SOUTH_AABB_BOTTOM
                Direction.WEST -> WEST_AABB_BOTTOM
                Direction.EAST -> EAST_AABB_BOTTOM
                else -> NORTH_AABB_BOTTOM
            }
        }
    }

    fun getPositionOfOtherPart(state: BlockState, pos: BlockPos): BlockPos {
        return if (state.get(PART) == PCPart.BOTTOM) {
            pos.up()
        } else {
            pos.down()
        }
    }

    fun getBasePosition(state: BlockState, pos: BlockPos): BlockPos {
        return if (isBase(state)) {
            pos
        } else {
            pos.down()
        }
    }

    private fun isBase(state: BlockState): Boolean = state.get(PART) == PCPart.BOTTOM

    override fun onPlaced(world: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack?) {
        world.setBlockState(pos.up(), state
            .with(PART, PCPart.TOP)
            .with(WATERLOGGED, world.getFluidState((pos.up())).fluid == Fluids.WATER)
                as BlockState, 3)
        world.updateNeighbors(pos, Blocks.AIR)
        state.updateNeighbors(world, pos, 3)
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState? {
        val abovePosition = blockPlaceContext.clickedPos.above()
        val world = blockPlaceContext.level
        if (world.getBlockState(abovePosition).canReplace(blockPlaceContext) && !world.isOutOfHeightLimit(abovePosition)) {
            return defaultBlockState()
                .setValue(HorizontalDirectionalBlock.FACING, blockPlaceContext.horizontalDirection)
                .setValue(PART, PCPart.BOTTOM)
                .setValue(WATERLOGGED, blockPlaceContext.level.getFluidState(blockPlaceContext.clickedPos).type == Fluids.WATER)
        }

        return null
    }

    override fun canSurvive(state: BlockState, world: LevelReader, pos: BlockPos): Boolean {
        val blockPos = pos.below()
        val blockState = world.getBlockState(blockPos)
        return if (state.getValue(PART) == PCPart.BOTTOM) blockState.isSideSolidFullSquare(world, blockPos, Direction.UP) else blockState.`is`(this)
    }

    override fun onBreak(world: Level, pos: BlockPos, state: BlockState, player: Player?): BlockState {
        if (!world.isClient && player?.isCreative == true) {
            var blockPos: BlockPos = BlockPos.ORIGIN
            var blockState: BlockState = state
            val part = state.get(PART)
            if (part == PCPart.TOP && world.getBlockState(pos.down().also { blockPos = it }).also { blockState = it }.isOf(state.block) && blockState.getValue(PART) == PCPart.BOTTOM) {
                val blockState2 = if (blockState.fluidState.isOf(Fluids.WATER)) Blocks.WATER.defaultState else Blocks.AIR.defaultState
                world.setBlockState(blockPos, blockState2, NOTIFY_ALL or SKIP_DROPS)
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, getRawIdFromState(blockState))
            }
        }
        return super.onBreak(world, pos, state, player)
    }

    override fun codec() = CODEC

    @Deprecated("Deprecated in Java")
    override fun isPathfindable(
        blockState: BlockState?,
        pathComputationType: PathComputationType
    ): Boolean = false

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(HorizontalDirectionalBlock.FACING)
        builder.add(PART)
        builder.add(ON)
        builder.add(WATERLOGGED)
    }

    @Deprecated("Deprecated in Java")
    override fun rotate(blockState: BlockState, rotation: BlockRotation) =
        blockState.with(
            HorizontalDirectionalBlock.FACING, rotation.rotate(blockState.getValue(
                HorizontalDirectionalBlock.FACING)))

    @Deprecated("Deprecated in Java")
    override fun mirror(blockState: BlockState, mirror: Mirror): BlockState {
        return blockState.rotate(mirror.getRotation(blockState.getValue(HorizontalDirectionalBlock.FACING)))
    }

    @Deprecated("Deprecated in Java")
    override fun onStateReplaced(state: BlockState, world: Level, pos: BlockPos?, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block)) super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun useWithoutItem(
        blockState: BlockState,
        world: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if (player !is ServerPlayer) return InteractionResult.SUCCESS

        val basePos = getBasePosition(blockState, blockPos)

        // Remove any duplicate block entities that may exist
        world.getBlockEntity(basePos.above())?.setRemoved()

        val baseEntity = world.getBlockEntity(basePos)
        if (baseEntity !is PCBlockEntity) return InteractionResult.SUCCESS

        if (player.isInBattle()) {
            player.sendSystemMessage(lang("pc.inbattle").red())
            return InteractionResult.SUCCESS
        }

        val pc = Cobblemon.storage.getPCForPlayer(player, baseEntity) ?: return InteractionResult.SUCCESS
        // TODO add event to check if they can open this PC? (answer: the getPCForPlayer should be where we do that)
        PCLinkManager.addLink(ProximityPCLink(pc, player.uuid, baseEntity))
        OpenPCPacket(pc.uuid).sendToPlayer(player)
        world.playSoundServer(
            position = blockPos.toVec3d(),
            sound = CobblemonSounds.PC_ON,
            volume = 0.5F,
            pitch = 1F
        )
        return InteractionResult.SUCCESS
    }

    override fun <T : BlockEntity> getTicker(world: Level, blockState: BlockState, BlockWithEntityType: BlockEntityType<T>) = validateTicker(BlockWithEntityType, CobblemonBlockEntities.PC, PCBlockEntity.TICKER::tick)

    @Deprecated("Deprecated in Java")
    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getFluidState(state: BlockState): FluidState? {
        return if (state.get(WATERLOGGED)) {
            Fluids.WATER.getStill(false)
        } else super.getFluidState(state)
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
        }

        val isPC = neighborState.`is`(this)
        val part = state.get(PART)
        if (!isPC && part == PCPart.TOP && neighborPos == pos.down()) {
            return Blocks.AIR.defaultState
        } else if (!isPC && part == PCPart.BOTTOM && neighborPos == pos.up()) {
            return Blocks.AIR.defaultState
        }

        return state
    }
}