package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.block.multiblock.builder.MultiblockStructureBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos

abstract class MultiblockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
    val multiblockBuilder: MultiblockStructureBuilder
    ) : BlockEntity(type, pos, state){

}
