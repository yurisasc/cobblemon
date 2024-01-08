package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos

class DisguisedBlockBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(CobblemonBlockEntities.DISGUISED_BLOCK, pos, state, ) {
    //Need this for transformation animations
}