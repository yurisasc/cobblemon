package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.fossil.NaturalMaterials
import com.cobblemon.mod.common.block.entity.ResurrectionMachineBlockEntity
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView

class ResurrectionMachineBlock(properties: Settings) : BlockWithEntity(properties) {

    override fun getRenderType(state: BlockState?) = BlockRenderType.MODEL
    override fun canPlaceAt(state: BlockState?, world: WorldView?, pos: BlockPos?): Boolean {
        return true
    }

    override fun <T : BlockEntity?> getTicker(
        world: World?,
        state: BlockState?,
        type: BlockEntityType<T>?
    ) = checkType(
        type,
        CobblemonBlockEntities.RESURRECTION_MACHINE,
        ResurrectionMachineBlockEntity.TICKER
    )

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return ResurrectionMachineBlockEntity(pos, state)
    }

    override fun onUse(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        player: PlayerEntity?,
        hand: Hand?,
        hit: BlockHitResult?
    ): ActionResult {
        if (world?.isClient == false) {
            //We could potentially use item tags instead of getting the IDs but its nice having everything in one file
            val stack = player?.getStackInHand(hand)
            val item = stack?.item
            val itemId = Registries.ITEM.getId(item)
            val entity = world.getBlockEntity(pos) as ResurrectionMachineBlockEntity
            if (NaturalMaterials.isNaturalMaterial(itemId)) {
                entity.organicMaterialInside += NaturalMaterials.getContent(itemId) ?: 0
                if (player?.isCreative != true) {
                    stack?.decrement(1)
                }
                return ActionResult.PASS
            }
        }

        return ActionResult.PASS

    }

}
