package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.util.math.BlockPos

class CobblemonChestBlockEntity(pos: BlockPos, state: BlockState) : ChestBlockEntity(pos, state) {

    override fun getType(): BlockEntityType<*> = CobblemonBlockEntities.GILDED_CHEST

}