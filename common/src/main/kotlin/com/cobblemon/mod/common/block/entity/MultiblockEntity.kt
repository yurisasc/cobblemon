package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.block.multiblock.MultiblockStructure
import com.cobblemon.mod.common.block.multiblock.builder.MultiblockStructureBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos

/**
 * Multiblock entities are responsible for containing a reference to the actual multiblock structure
 */
abstract class MultiblockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
    var multiblockBuilder: MultiblockStructureBuilder?
    ) : BlockEntity(type, pos, state){
        abstract var multiblockStructure: MultiblockStructure?
}
