package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.fossil.FossilVariant
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.util.math.BlockPos

class ResurrectionMachineBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(CobblemonBlockEntities.RESURRECTION_MACHINE, pos, state) {
    val ticksPerMinute = 1200
    val fossilInside: FossilVariant? = null
    var organicMaterialInside = 0
    var timeRemaining = ticksPerMinute * 5

    companion object {
        val TICKER = BlockEntityTicker<ResurrectionMachineBlockEntity> { _, _, _, blockEntity ->
            if (blockEntity.timeRemaining > 0) {
                blockEntity.timeRemaining--
            }
        }
    }
}
