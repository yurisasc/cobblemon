package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.block.multiblock.MultiblockStructure
import com.cobblemon.mod.common.block.multiblock.builder.MultiblockStructureBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.util.math.BlockPos

class FossilMultiblockEntity(
    pos: BlockPos,
    state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder,
    ) : MultiblockEntity(CobblemonBlockEntities.FOSSIL_MULTIBLOCK, pos, state, multiblockBuilder) {
    override var multiblockStructure: MultiblockStructure? = null

    companion object {
        val TICKER = BlockEntityTicker<FossilMultiblockEntity> { _, _, _, blockEntity ->
            if (blockEntity.multiblockStructure != null) {
                blockEntity.multiblockStructure!!.tick()
            }
        }
    }
}
