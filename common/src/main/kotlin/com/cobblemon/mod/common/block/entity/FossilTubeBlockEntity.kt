package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.block.fossilmachine.FossilTubeBlock
import com.cobblemon.mod.common.block.multiblock.builder.MultiblockStructureBuilder
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

class FossilTubeBlockEntity(
    pos: BlockPos, state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder
) : FossilMultiblockEntity(pos, state, multiblockBuilder, CobblemonBlockEntities.FOSSIL_TUBE) {
    var isOn = false

    init {
        isOn = state.get(FossilTubeBlock.PART) == FossilTubeBlock.TubePart.BOTTOM
    }
}