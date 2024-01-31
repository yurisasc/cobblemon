package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.block.entity.EggBlockEntity
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class EggBlock(settings: Settings?) : BlockWithEntity(settings) {
    override fun onPlaced(
        world: World?,
        pos: BlockPos?,
        state: BlockState?,
        placer: LivingEntity?,
        itemStack: ItemStack?
    ) {
        val entity = world?.getBlockEntity(pos) as? EggBlockEntity
        entity?.let { it.egg = Egg.fromNbt(itemStack?.nbt?.get(DataKeys.EGG) as NbtCompound) }
        super.onPlaced(world, pos, state, placer, itemStack)
    }
    override fun getRenderType(state: BlockState?) = BlockRenderType.ENTITYBLOCK_ANIMATED
    override fun createBlockEntity(pos: BlockPos, state: BlockState) = EggBlockEntity(pos, state)
}