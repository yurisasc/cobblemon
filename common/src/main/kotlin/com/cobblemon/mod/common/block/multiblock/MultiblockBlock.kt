package com.cobblemon.mod.common.block.multiblock

import com.cobblemon.mod.common.block.entity.FossilMultiblockEntity
import com.cobblemon.mod.common.block.entity.MultiblockEntity
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * A block that can be part of a [MultiblockStructure]
 */
abstract class MultiblockBlock(properties: Settings) : BlockWithEntity(properties) {

    override fun onPlaced(
        world: World,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        itemStack: ItemStack?
    ) {
        super.onPlaced(world, pos, state, placer, itemStack)
        if (world is ServerWorld) {
            val multiblockEntity = world.getBlockEntity(pos) as MultiblockEntity
            multiblockEntity.multiblockBuilder.validate(world)
        }
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return createMultiBlockEntity(pos, state)
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    abstract fun createMultiBlockEntity(pos: BlockPos, state: BlockState): FossilMultiblockEntity

}
