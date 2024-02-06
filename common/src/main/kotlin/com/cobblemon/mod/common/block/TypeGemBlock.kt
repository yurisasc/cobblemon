package com.cobblemon.mod.common.block

import net.minecraft.block.*
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView

class TypeGemBlock(
    settings: Settings,
    stage: Int,
    height: Int,
    xzOffset: Int,
    nextStage: Block?,
    val baseBlock: Block
) : GrowableStoneBlock(settings, stage, height, xzOffset, nextStage) {
    override fun canGrow(pos: BlockPos, world: BlockView): Boolean {
        val state = world.getBlockState(pos)
        val dir = state.get(FACING).opposite
        return (world.getBlockState(pos.offset(dir)).block == baseBlock)
    }
}