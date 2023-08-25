package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.block.multiblock.builder.MultiblockStructureBuilder
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

class FossilTubeEntity(
    pos: BlockPos, state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder
) : FossilMultiblockEntity(pos, state, multiblockBuilder) {

}