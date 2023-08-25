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
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView


class FossilTubeBlock(properties: Settings) : MultiblockBlock(properties) {
    init {
        defaultState = defaultState
            .with(HorizontalFacingBlock.FACING, Direction.NORTH)
            .with(PART, TubePart.BOTTOM)
            .with(ON, true)
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
        world.setBlockState(pos.up(), state.with(PART, TubePart.TOP) as BlockState, 3)
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
                .with(HorizontalFacingBlock.FACING, blockPlaceContext.horizontalPlayerFacing)
                .with(PART, TubePart.BOTTOM)
        }

        return null
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val blockPos = pos.down()
        val blockState = world.getBlockState(blockPos)
        return if (state.get(PART) == TubePart.BOTTOM) blockState.isSideSolidFullSquare(world, blockPos, Direction.UP) else blockState.isOf(this)
    }
    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(HorizontalFacingBlock.FACING)
        builder.add(PART)
        builder.add(ON)
    }

    @Deprecated("Deprecated in Java")
    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos?, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block)) super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        if (state.get(PART) == TubePart.TOP) {
            var shape = VoxelShapes.cuboid(0.0625, 0.0, 0.0625, 0.9375, 0.8125, 0.9375)
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0, 0.8125, 0.0, 1.0, 1.0, 1.0));
            return shape;
        }

        else {
            var shape = VoxelShapes.cuboid(0.0625, 0.1875, 0.0625, 0.9375, 1.0, 0.9375)
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.1875, 1.0));
            return shape
        }
    }

    enum class TubePart(private val label: String) : StringIdentifiable {
        TOP("top"),
        BOTTOM("bottom");
        override fun asString() = label
    }

    companion object {
        val PART = EnumProperty.of("part", TubePart::class.java)
        val ON = BooleanProperty.of("on")
    }
}
