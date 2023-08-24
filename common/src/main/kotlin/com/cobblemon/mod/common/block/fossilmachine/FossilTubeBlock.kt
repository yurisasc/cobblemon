package com.cobblemon.mod.common.block.fossilmachine

import com.cobblemon.mod.common.block.PCBlock
import com.cobblemon.mod.common.block.entity.FossilMultiblockEntity
import com.cobblemon.mod.common.block.multiblock.MultiblockBlock
import com.cobblemon.mod.common.block.multiblock.builder.ResurrectionMachineMultiblockBuilder
import net.minecraft.block.*
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView

class FossilTubeBlock(properties: Settings) : MultiblockBlock(properties), Waterloggable {
    init {
        defaultState = defaultState
            .with(Properties.FACING, Direction.NORTH)
            .with(PART, TubePart.BOTTOM)
            .with(WATERLOGGED, false)
            .with(ON, false)
    }

    fun getPositionOfOtherPart(state: BlockState, pos: BlockPos): BlockPos {
        return if (state.get(PART) == TubePart.BOTTOM) {
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



    private fun isBase(state: BlockState): Boolean = state.get(PART) == TubePart.BOTTOM

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity?) {
        super.onBreak(world, pos, state, player)
        val otherPart = world.getBlockState(getPositionOfOtherPart(state, pos))
        if (otherPart.block is FossilTubeBlock) {
            world.setBlockState(getPositionOfOtherPart(state, pos), Blocks.AIR.defaultState, 35)
            world.syncWorldEvent(player, 2001, getPositionOfOtherPart(state, pos), getRawIdFromState(otherPart))
        }
    }

    override fun onPlaced(
        world: World,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        itemStack: ItemStack?
    ) {
        //Place the full block before we call to super to validate the multiblock
        world.setBlockState(pos.up(), state
            .with(PART, TubePart.TOP)
            .with(PCBlock.WATERLOGGED, world.getFluidState((pos.up())).fluid == Fluids.WATER)
                as BlockState, 3)
        world.updateNeighbors(pos, Blocks.AIR)
        state.updateNeighbors(world, pos, 3)
        super.onPlaced(world, pos, state, placer, itemStack)
    }

    override fun createMultiBlockEntity(pos: BlockPos, state: BlockState): FossilMultiblockEntity {
        return FossilMultiblockEntity(
            pos, state, ResurrectionMachineMultiblockBuilder(pos)
        )
    }

    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState? {
        val abovePosition = blockPlaceContext.blockPos.up()
        val world = blockPlaceContext.world
        if (world.getBlockState(abovePosition).canReplace(blockPlaceContext) && !world.isOutOfHeightLimit(abovePosition)) {
            return defaultState
                .with(Properties.FACING, blockPlaceContext.horizontalPlayerFacing)
                .with(PART, TubePart.BOTTOM)
                .with(WATERLOGGED, blockPlaceContext.world.getFluidState(blockPlaceContext.blockPos).fluid == Fluids.WATER)
        }

        return null
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val blockPos = pos.down()
        val blockState = world.getBlockState(blockPos)
        return if (state.get(PART) == TubePart.BOTTOM) blockState.isSideSolidFullSquare(world, blockPos, Direction.UP) else blockState.isOf(this)
    }
    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Properties.FACING)
        builder.add(PART)
        builder.add(WATERLOGGED)
        builder.add(ON)
    }

    @Deprecated("Deprecated in Java")
    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos?, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block)) super.onStateReplaced(state, world, pos, newState, moved)
    }

    @Deprecated("Deprecated in Java")
    override fun getFluidState(state: BlockState): FluidState? {
        return if (state.get(PCBlock.WATERLOGGED)) {
            Fluids.WATER.getStill(false)
        } else super.getFluidState(state)
    }

    @Deprecated("Deprecated in Java")
    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction?,
        neighborState: BlockState?,
        world: WorldAccess,
        pos: BlockPos?,
        neighborPos: BlockPos?
    ): BlockState? {
        if (state.get(PCBlock.WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
    }

    enum class TubePart(private val label: String) : StringIdentifiable {
        TOP("top"),
        BOTTOM("bottom");
        override fun asString() = label
    }

    companion object {
        val PART = EnumProperty.of("part", TubePart::class.java)
        val WATERLOGGED = BooleanProperty.of("waterlogged")
        val ON = BooleanProperty.of("on")
    }
}
