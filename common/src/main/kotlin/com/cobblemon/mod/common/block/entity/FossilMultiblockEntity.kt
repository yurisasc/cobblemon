package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.block.multiblock.builder.MultiblockStructureBuilder
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

class FossilMultiblockEntity(
    pos: BlockPos,
    state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder,
    ) : MultiblockEntity(CobblemonBlockEntities.FOSSIL_MULTIBLOCK, pos, state, multiblockBuilder) {

}
