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
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView

class PCBlock(properties: Settings): BlockWithEntity(properties) {
    companion object {
        val PART = EnumProperty.of("part", PCPart::class.java)
        val ON = BooleanProperty.of("on")

        private val NORTH_AABB_TOP = VoxelShapes.union(
            VoxelShapes.cuboid(0.1875, 0.0, 0.0, 0.8125, 0.875, 0.125),
            VoxelShapes.cuboid(0.125, 0.8125, 0.125, 0.875, 0.9375, 0.6875),
            VoxelShapes.cuboid(0.125, 0.125, 0.125, 0.875, 0.8125, 0.625),
            VoxelShapes.cuboid(0.0625, 0.0, 0.125, 0.125, 0.9375, 0.6875),
            VoxelShapes.cuboid(0.125, 0.0, 0.125, 0.875, 0.125, 0.6875),
            VoxelShapes.cuboid(0.875, 0.0, 0.125, 0.9375, 0.9375, 0.6875),
            VoxelShapes.cuboid(0.125, 0.0, 0.6875, 0.875, 0.0625, 0.875)
        )

        private val SOUTH_AABB_TOP = VoxelShapes.union(
            VoxelShapes.cuboid(0.1875, 0.0, 0.875, 0.8125, 0.875, 1.0),
            VoxelShapes.cuboid(0.125, 0.8125, 0.3125, 0.875, 0.9375, 0.875),
            VoxelShapes.cuboid(0.125, 0.125, 0.375, 0.875, 0.8125, 0.875),
            VoxelShapes.cuboid(0.875, 0.0, 0.3125, 0.9375, 0.9375, 0.875),
            VoxelShapes.cuboid(0.125, 0.0, 0.3125, 0.875, 0.125, 0.875),
            VoxelShapes.cuboid(0.0625, 0.0, 0.3125, 0.125, 0.9375, 0.875),
            VoxelShapes.cuboid(0.125, 0.0, 0.125, 0.875, 0.0625, 0.3125)
        )

        private val WEST_AABB_TOP = VoxelShapes.union(
            VoxelShapes.cuboid(0.0, 0.0, 0.1875, 0.125, 0.875, 0.8125),
            VoxelShapes.cuboid(0.125, 0.8125, 0.125, 0.6875, 0.9375, 0.875),
            VoxelShapes.cuboid(0.125, 0.125, 0.125, 0.625, 0.8125, 0.875),
            VoxelShapes.cuboid(0.125, 0.0, 0.0625, 0.6875, 0.9375, 0.125),
            VoxelShapes.cuboid(0.125, 0.0, 0.125, 0.6875, 0.125, 0.875),
            VoxelShapes.cuboid(0.125, 0.0, 0.875, 0.6875, 0.9375, 0.9375),
            VoxelShapes.cuboid(0.6875, 0.0, 0.125, 0.875, 0.0625, 0.875)
        )

        private val EAST_AABB_TOP = VoxelShapes.union(
            VoxelShapes.cuboid(0.875, 0.0, 0.1875, 1.0, 0.875, 0.8125),
            VoxelShapes.cuboid(0.3125, 0.8125, 0.125, 0.875, 0.9375, 0.875),
            VoxelShapes.cuboid(0.375, 0.125, 0.125, 0.875, 0.8125, 0.875),
            VoxelShapes.cuboid(0.3125, 0.0, 0.0625, 0.875, 0.9375, 0.125),
            VoxelShapes.cuboid(0.3125, 0.0, 0.125, 0.875, 0.125, 0.875),
            VoxelShapes.cuboid(0.3125, 0.0, 0.875, 0.875, 0.9375, 0.9375),
            VoxelShapes.cuboid(0.125, 0.0, 0.125, 0.3125, 0.0625, 0.875)
        )

        private val NORTH_AABB_BOTTOM = VoxelShapes.union(
            VoxelShapes.cuboid(0.0625, 0.0625, 0.125, 0.9375, 1.0, 0.9375),
            VoxelShapes.cuboid(0.125, 0.0, 0.125, 0.875, 0.0625, 0.875),
            VoxelShapes.cuboid(0.1875, 0.0, 0.0, 0.8125, 1.0, 0.125)
        )

        private val SOUTH_AABB_BOTTOM = VoxelShapes.union(
            VoxelShapes.cuboid(0.0625, 0.0625, 0.0625, 0.9375, 1.0, 0.875),
            VoxelShapes.cuboid(0.125, 0.0, 0.125, 0.875, 0.0625, 0.875),
            VoxelShapes.cuboid(0.1875, 0.0, 0.875, 0.8125, 1.0, 1.0)
        )

        private val WEST_AABB_BOTTOM = VoxelShapes.union(
            VoxelShapes.cuboid(0.125, 0.0625, 0.0625, 0.9375, 1.0, 0.9375),
            VoxelShapes.cuboid(0.125, 0.0, 0.125, 0.875, 0.0625, 0.875),
            VoxelShapes.cuboid(0.0, 0.0, 0.1875, 0.125, 1.0, 0.8125)
        )

        private val EAST_AABB_BOTTOM = VoxelShapes.union(
            VoxelShapes.cuboid(0.0625, 0.0625, 0.0625, 0.875, 1.0, 0.9375),
            VoxelShapes.cuboid(0.125, 0.0, 0.125, 0.875, 0.0625, 0.875),
            VoxelShapes.cuboid(0.875, 0.0, 0.1875, 1.0, 1.0, 0.8125)
        )
    }

    enum class PCPart(private val label: String) : StringIdentifiable {
        TOP("top"),
        BOTTOM("bottom");
        override fun asString() = label
    }

    init {
        defaultState = this.stateManager.defaultState.with(HorizontalFacingBlock.FACING, Direction.NORTH)
            .with(PART, PCPart.BOTTOM)
            .with(ON, false)
    }

    override fun createBlockEntity(blockPos: BlockPos, blockState: BlockState) = if (blockState.get(PART) == PCPart.BOTTOM) PCBlockEntity(blockPos, blockState) else null

    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, collisionContext: ShapeContext): VoxelShape {
        return if (blockState.get(PART) == PCPart.TOP)  {
            when (blockState.get(HorizontalFacingBlock.FACING)) {
                Direction.SOUTH -> SOUTH_AABB_TOP
                Direction.WEST -> WEST_AABB_TOP
                Direction.EAST -> EAST_AABB_TOP
                else -> NORTH_AABB_TOP
            }
        } else {
            when (blockState.get(HorizontalFacingBlock.FACING)) {
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

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity?) {
        super.onBreak(world, pos, state, player)
        val otherPart = world.getBlockState(getPositionOfOtherPart(state, pos))
        if (otherPart.block is PCBlock) {
            world.setBlockState(getPositionOfOtherPart(state, pos), Blocks.AIR.defaultState, 35)
            world.syncWorldEvent(player, 2001, getPositionOfOtherPart(state, pos), Block.getRawIdFromState(otherPart))
        }
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack?) {
        world.setBlockState(pos.up(), state.with(PART, PCPart.TOP) as BlockState, 3)
        world.updateNeighbors(pos, Blocks.AIR)
        state.updateNeighbors(world, pos, 3)
    }

    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState? {
        val abovePosition = blockPlaceContext.blockPos.up()
        val world = blockPlaceContext.world
        if (world.getBlockState(abovePosition).canReplace(blockPlaceContext) && !world.isOutOfHeightLimit(abovePosition)) {
            return defaultState
                .with(HorizontalFacingBlock.FACING, blockPlaceContext.horizontalPlayerFacing)
                .with(PART, PCPart.BOTTOM)
        }

        return null
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val blockPos = pos.down()
        val blockState = world.getBlockState(blockPos)
        return if (state.get(PART) == PCPart.BOTTOM) blockState.isSideSolidFullSquare(world, blockPos, Direction.UP) else blockState.isOf(this)
    }

    @Deprecated("Deprecated in Java")
    override fun canPathfindThrough(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, pathComputationType: NavigationType) = false

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(HorizontalFacingBlock.FACING)
        builder.add(PART)
        builder.add(ON)
    }

    @Deprecated("Deprecated in Java")
    override fun rotate(blockState: BlockState, rotation: BlockRotation) =
        blockState.with(HorizontalFacingBlock.FACING, rotation.rotate(blockState.get(HorizontalFacingBlock.FACING)))

    @Deprecated("Deprecated in Java")
    override fun mirror(blockState: BlockState, mirror: BlockMirror): BlockState {
        return blockState.rotate(mirror.getRotation(blockState.get(HorizontalFacingBlock.FACING)))
    }

    @Deprecated("Deprecated in Java")
    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos?, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block)) super.onStateReplaced(state, world, pos, newState, moved)
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(
        blockState: BlockState,
        world: World,
        blockPos: BlockPos,
        player: PlayerEntity,
        interactionHand: Hand,
        blockHitResult: BlockHitResult
    ): ActionResult {
        if (player !is ServerPlayerEntity) return ActionResult.SUCCESS

        val basePos = getBasePosition(blockState, blockPos)

        // Remove any duplicate block entities that may exist
        world.getBlockEntity(basePos.up())?.markRemoved()

        val baseEntity = world.getBlockEntity(basePos)
        if (baseEntity !is PCBlockEntity) return ActionResult.SUCCESS

        if (player.isInBattle()) {
            player.sendMessage(lang("pc.inbattle").red())
            return ActionResult.SUCCESS
        }

        val pc = Cobblemon.storage.getPCForPlayer(player, baseEntity) ?: return ActionResult.SUCCESS
        // TODO add event to check if they can open this PC?
        PCLinkManager.addLink(ProximityPCLink(pc, player.uuid, baseEntity))
        OpenPCPacket(pc.uuid).sendToPlayer(player)
        world.playSoundServer(
            position = blockPos.toVec3d(),
            sound = CobblemonSounds.PC_ON,
            volume = 1F,
            pitch = 1F
        )
        return ActionResult.SUCCESS
    }

    override fun <T : BlockEntity> getTicker(world: World, blockState: BlockState, BlockWithEntityType: BlockEntityType<T>) =  checkType(BlockWithEntityType, CobblemonBlockEntities.PC, PCBlockEntity.TICKER::tick)

    @Deprecated("Deprecated in Java")
    override fun getRenderType(blockState: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }
}