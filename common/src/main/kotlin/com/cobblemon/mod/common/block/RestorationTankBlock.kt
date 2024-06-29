/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.api.multiblock.MultiblockBlock
import com.cobblemon.mod.common.api.multiblock.MultiblockEntity
import com.cobblemon.mod.common.block.entity.FossilMultiblockEntity
import com.cobblemon.mod.common.block.entity.RestorationTankBlockEntity
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockBuilder
import com.mojang.serialization.MapCodec
import net.minecraft.block.*
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.inventory.SidedInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerLevel
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.StringRepresentable
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.*
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape


class RestorationTankBlock(settings: Properties) : MultiblockBlock(properties), InventoryProvider {

    init {
        registerDefaultState(stateDefinition.any()
            .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
            .setValue(PART, TankPart.BOTTOM)
            .setValue(TRIGGERED, false)
            .setValue(ON, false))
    }

    fun getPositionOfOtherPart(state: BlockState, pos: BlockPos): BlockPos {
        return if (state.get(PART) == TankPart.BOTTOM) {
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

    private fun isBase(state: BlockState): Boolean = state.get(PART) == TankPart.BOTTOM

    override fun playerWillDestroy(world: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        val result = super.playerWillDestroy(world, pos, state, player)
        if (!world.isClient) {
            val otherPart = world.getBlockState(getPositionOfOtherPart(state, pos))

            if (otherPart.block is RestorationTankBlock) {
                world.setBlockState(getPositionOfOtherPart(state, pos), Blocks.AIR.defaultState, Block.NOTIFY_ALL)
                world.syncWorldEvent(
                    player,
                    WorldEvents.BLOCK_BROKEN,
                    getPositionOfOtherPart(state, pos),
                    getRawIdFromState(otherPart)
                )
            }
        }

        return result
    }

    override fun onPlaced(
        world: Level,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        itemStack: ItemStack?
    ) {
        //Place the full block before we call to super to validate the multiblock
        world.setBlockState(pos.up(), state.with(PART, TankPart.TOP) as BlockState, Block.NOTIFY_ALL)
        world.updateNeighbors(pos, Blocks.AIR)
        state.updateNeighbors(world, pos, Block.NOTIFY_ALL)
        super.setPlacedBy(world, pos, state, placer, itemStack)
    }

    @Deprecated("Deprecated in Java")
    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult {
        if(state.get(PART) == TankPart.TOP) {
            // TankTop isn't reliably up to date on clients, need to look at the bottom half
            val tankBottomPos = pos.down()
            val tankBottomState = world.getBlockState(tankBottomPos)
            if(tankBottomState.block.equals(CobblemonBlocks.RESTORATION_TANK.asBlock()) && tankBottomState.get(PART) == TankPart.BOTTOM) {
                return super.useWithoutItem(tankBottomState, world, tankBottomPos, player, hit)
            }
        }
        return super.useWithoutItem(state, world, pos, player, hit)
    }

    override fun createMultiBlockEntity(pos: BlockPos, state: BlockState): FossilMultiblockEntity {
        return if (state.get(PART) == TankPart.BOTTOM) {
            RestorationTankBlockEntity(pos, state, FossilMultiblockBuilder(pos))
        } else {
            FossilMultiblockEntity(pos, state, FossilMultiblockBuilder(pos))
        }
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState? {
        val abovePosition = blockPlaceContext.clickedPos.above()
        val world = blockPlaceContext.level
        if (world.getBlockState(abovePosition).canReplace(blockPlaceContext) && !world.isOutOfHeightLimit(abovePosition)) {
            return defaultBlockState()
                .setValue(HorizontalDirectionalBlock.FACING, blockPlaceContext.horizontalDirection)
                .setValue(PART, TankPart.BOTTOM)
        }

        return null
    }

    override fun canSurvive(state: BlockState, world: LevelReader, pos: BlockPos): Boolean {
        val blockPos = pos.below()
        val blockState = world.getBlockState(blockPos)
        return if (state.getValue(PART) == TankPart.BOTTOM) true else blockState.`is`(this)
    }
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(HorizontalDirectionalBlock.FACING)
        builder.add(PART)
        builder.add(TRIGGERED)
        builder.add(ON)
    }

    @Deprecated("Deprecated in Java")
    override fun hasComparatorOutput(state: BlockState?): Boolean {
        // TODO: return false if not attached to a multiblock structure
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun getComparatorOutput(state: BlockState, world: Level?, pos: BlockPos?): Int {
        if(world == null || pos == null) {
            return 0
        }
        val tankEntity = world.getBlockEntity(pos) as? MultiblockEntity
        val multiBlockEntity = tankEntity?.multiblockStructure
        if(multiBlockEntity != null) {
            return multiBlockEntity.getComparatorOutput(state, world, pos)
        }
        return 0
    }

    @Deprecated("Deprecated in Java")
    override fun onStateReplaced(state: BlockState, world: Level, pos: BlockPos?, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block)) super.onStateReplaced(state, world, pos, newState, moved)
    }

    @Deprecated("Deprecated in Java")
    override fun neighborUpdate(state: BlockState, world: Level, pos: BlockPos, sourceBlock: Block?, sourcePos: BlockPos?, notify: Boolean) {
        val bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up())
        val bl2 = state.get(TRIGGERED)
        if (bl && !bl2) {
            world.scheduleBlockTick(pos, this, 4)
            world.setBlockState(pos, state.with(TRIGGERED, true) as BlockState, NO_REDRAW)
        } else if (!bl && bl2) {
            world.setBlockState(pos, state.with(TRIGGERED, false) as BlockState, NO_REDRAW)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun scheduledTick(state: BlockState?, world: ServerLevel?, pos: BlockPos?, random: Random?) {
        if(world == null || pos == null) {
            return
        }
        val tankEntity = world.getBlockEntity(pos) as? MultiblockEntity
        tankEntity?.multiblockStructure?.onTriggerEvent(state, world, pos, random)
    }

    override fun getShape(
        state: BlockState,
        blockGetter: BlockGetter,
        pos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape {
        return if (state.getValue(PART) == TankPart.TOP) {
            var shape = Shapes.box(0.0625, 0.0, 0.0625, 0.9375, 0.8125, 0.9375)
            shape = Shapes.or(shape, Shapes.box(0.0, 0.8125, 0.0, 1.0, 1.0, 1.0))
            shape
        } else {
            var shape = Shapes.box(0.0625, 0.1875, 0.0625, 0.9375, 1.0, 0.9375)
            shape = Shapes.or(shape, Shapes.box(0.0, 0.0, 0.0, 1.0, 0.1875, 1.0))
            shape
        }
    }
    override fun getInventory(
        state: BlockState,
        world: WorldAccess,
        pos: BlockPos
    ): SidedInventory {
        val tankEntity =
            (if (state.get(PART) == TankPart.TOP) world.getBlockEntity(pos.down())
            else world.getBlockEntity(pos))
        return if(tankEntity != null && tankEntity is RestorationTankBlockEntity) tankEntity.inv else DummyInventory()
    }

    enum class TankPart(private val label: String) : StringRepresentable {
        TOP("top"),
        BOTTOM("bottom");
        override fun getSerializedName(): String = label
    }

    @Deprecated("Deprecated in Java")
    override fun isPathfindable(state: BlockState?, type: PathComputationType): Boolean {
        return false
    }

    override fun codec(): MapCodec<out BaseEntityBlock> {
        return CODEC
    }

    companion object {
        val CODEC = createCodec(::RestorationTankBlock)

        val PART = EnumProperty.create("part", TankPart::class.java)
        val TRIGGERED = BlockStateProperties.TRIGGERED
        val ON = BooleanProperty.create("on")

        class DummyInventory : SimpleInventory(0), SidedInventory {
            override fun getAvailableSlots(side: Direction): IntArray {
                return IntArray(0)
            }

            override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?): Boolean {
                return false
            }

            override fun canExtract(slot: Int, stack: ItemStack, dir: Direction): Boolean {
                return false
            }
        }
    }
}