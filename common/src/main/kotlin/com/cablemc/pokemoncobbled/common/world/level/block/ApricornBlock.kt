package com.cablemc.pokemoncobbled.common.world.level.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BonemealableBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import java.util.*

class ApricornBlock(properties: Properties) : HorizontalDirectionalBlock(properties), BonemealableBlock {

    override fun isValidBonemealTarget(blockGetter: BlockGetter, blockPos: BlockPos, blockState: BlockState, bl: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun isBonemealSuccess(level: Level, random: Random, blockPos: BlockPos, blockState: BlockState): Boolean {
        TODO("Not yet implemented")
    }

    override fun performBonemeal(serverLevel: ServerLevel, random: Random, blockPos: BlockPos, blockState: BlockState) {
        TODO("Not yet implemented")
    }
    
}