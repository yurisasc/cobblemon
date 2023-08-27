package com.cobblemon.mod.common.block.entity.fossil

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.block.entity.MultiblockEntity
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import com.cobblemon.mod.common.block.multiblock.MultiblockStructure
import com.cobblemon.mod.common.block.multiblock.builder.MultiblockStructureBuilder
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.util.math.BlockPos

open class FossilMultiblockEntity(
    pos: BlockPos,
    state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder,
    type: BlockEntityType<*> = CobblemonBlockEntities.FOSSIL_MULTIBLOCK
    ) : MultiblockEntity(type, pos, state, multiblockBuilder) {
    override var multiblockStructure: MultiblockStructure? = null
        set(structure) {
            field = structure
            masterBlockPos = structure?.controllerBlockPos
        }
        get() {
            return if (field != null) {
                field
            } else if (masterBlockPos != null && world != null) {
                field = (world?.getBlockEntity(masterBlockPos) as FossilMultiblockEntity).multiblockStructure
                field
            } else {
                null
            }
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

}
