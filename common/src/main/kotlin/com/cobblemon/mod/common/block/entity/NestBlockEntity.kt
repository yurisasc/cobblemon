package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos

class NestBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(CobblemonBlockEntities.NEST, pos, state) {
    var egg: Egg? = null
    var renderState: BlockEntityRenderState? = null

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)

    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        egg = Egg.fromNbt(nbt.getCompound(DataKeys.EGG))
        renderState?.needsRebuild = true
    }

}