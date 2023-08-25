package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import com.cobblemon.mod.common.block.multiblock.MultiblockStructure
import com.cobblemon.mod.common.block.multiblock.builder.MultiblockStructureBuilder
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.util.math.BlockPos

class FossilMultiblockEntity(
    pos: BlockPos,
    state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder,
    ) : MultiblockEntity(CobblemonBlockEntities.FOSSIL_MULTIBLOCK, pos, state, multiblockBuilder) {
    override var multiblockStructure: MultiblockStructure? = null
        set(structure) {
            field = structure
            masterBlockPos = structure?.controllerBlockPos
        }

    override var masterBlockPos: BlockPos? = null
    override fun readNbt(nbt: NbtCompound?) {
        if (nbt?.contains(DataKeys.MULTIBLOCK_STORAGE) == true) {
            multiblockStructure = FossilMultiblockStructure.fromNbt(nbt.getCompound(DataKeys.MULTIBLOCK_STORAGE))
        }
        if (nbt?.contains(DataKeys.CONTROLLER_BLOCK) == true) {
            masterBlockPos = NbtHelper.toBlockPos(nbt.getCompound(DataKeys.CONTROLLER_BLOCK))
        }
    }


    companion object {
        val TICKER = BlockEntityTicker<FossilMultiblockEntity> { _, _, _, blockEntity ->
            if (blockEntity.multiblockStructure != null) {
                blockEntity.multiblockStructure!!.tick()
            }
        }
    }
}
