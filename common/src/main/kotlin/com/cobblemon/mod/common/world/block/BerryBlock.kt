package com.cobblemon.mod.common.world.block

import com.cobblemon.mod.common.tags.CobblemonBlockTags
import com.cobblemon.mod.common.world.block.entity.BerryBlockEntity
import net.minecraft.block.*
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView

class BerryBlock(settings: Settings) : BlockWithEntity(settings), Fertilizable {

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = BerryBlockEntity(pos, state)

    override fun isFertilizable(world: BlockView?, pos: BlockPos?, state: BlockState?, isClient: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun canGrow(world: World, random: Random, pos: BlockPos, state: BlockState): Boolean {
        TODO("Not yet implemented")
    }

    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) {
        TODO("Not yet implemented")
    }

    @Deprecated("Deprecated in Java")
    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        return world.getBlockState(pos.down()).isIn(CobblemonBlockTags.BERRY_SOIL)
    }

    @Deprecated("Deprecated in Java")
    override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, neighborState: BlockState, world: WorldAccess, pos: BlockPos, neighborPos: BlockPos): BlockState {
        return if (state.canPlaceAt(world, pos)) super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos) else Blocks.AIR.defaultState
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
        super.appendProperties(builder)
    }
}