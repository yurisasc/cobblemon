package com.cablemc.pokemoncobbled.common.world.level.block.entity

import com.cablemc.pokemoncobbled.common.CobbledBlockEntities
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos

class PCBlockEntity(
    blockPos: BlockPos,
    blockState: BlockState
) : BlockEntity(CobbledBlockEntities.PC.get(), blockPos, blockState) {
}