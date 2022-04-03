package com.cablemc.pokemoncobbled.common.world.level.block.entity

import com.cablemc.pokemoncobbled.common.CobbledBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class HealingMachineBlockEntity(
    blockPos: BlockPos,
    blockState: BlockState) : BlockEntity(CobbledBlockEntities.HEALING_MACHINE.get(), blockPos, blockState
) {

}