package com.cobblemon.mod.common.block.fossilmachine

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.block.entity.fossil.FossilMultiblockEntity
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import com.cobblemon.mod.common.block.multiblock.MultiblockBlock
import com.cobblemon.mod.common.block.multiblock.builder.ResurrectionMachineMultiblockBuilder
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class FossilMonitorBlock(properties: Settings) : MultiblockBlock(properties) {
    init {
        defaultState = defaultState.with(Properties.FACING, Direction.NORTH)
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
        return defaultState.with(Properties.FACING, blockPlaceContext.horizontalPlayerFacing)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Properties.FACING)
    }

}
