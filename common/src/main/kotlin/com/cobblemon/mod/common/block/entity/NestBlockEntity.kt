package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos

class NestBlockEntity(val pos: BlockPos, val state: BlockState) : BlockEntity(CobblemonBlockEntities.NEST, pos, state) {
    var egg: Egg? = null


}