package com.cobblemon.mod.common.block.fossilmachine

import com.cobblemon.mod.common.block.entity.FossilMultiblockEntity
import com.cobblemon.mod.common.block.multiblock.MultiblockBlock
import com.cobblemon.mod.common.block.multiblock.builder.ResurrectionMachineMultiblockBuilder
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class FossilCompartmentBlock(properties: Settings) : MultiblockBlock(properties){
    init {
        defaultState = defaultState
            .with(HorizontalFacingBlock.FACING, Direction.NORTH)
            .with(ON, true)
    }

    override fun createMultiBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): FossilMultiblockEntity {
        return FossilMultiblockEntity(
            pos, state, ResurrectionMachineMultiblockBuilder(pos)
        )
    }

    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState? {
        return defaultState.with(HorizontalFacingBlock.FACING, blockPlaceContext.horizontalPlayerFacing)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(HorizontalFacingBlock.FACING)
        builder.add(ON)
    }

    companion object {
        val ON = BooleanProperty.of("on")
    }
}
