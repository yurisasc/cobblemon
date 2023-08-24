package com.cobblemon.mod.common.block.multiblock

import com.cobblemon.mod.common.api.fossil.FossilVariant
import com.cobblemon.mod.common.api.fossil.NaturalMaterials
import com.cobblemon.mod.common.block.entity.FossilMultiblockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class FossilMultiblockStructure(
    val monitorPos: BlockPos,
    val compartmentPos: BlockPos,
    val tubeBasePos: BlockPos
) : MultiblockStructure {
    val ticksPerMinute = 1200
    val fossilInside: FossilVariant? = null
    var organicMaterialInside = 0
    var timeRemaining = ticksPerMinute * 5

    override fun onUse(
        blockState: BlockState,
        world: World,
        blockPos: BlockPos,
        player: PlayerEntity,
        interactionHand: Hand,
        blockHitResult: BlockHitResult
    ): ActionResult {
        if (!world.isClient) {
            //We could potentially use item tags instead of getting the IDs but its nice having everything in one file
            val stack = player.getStackInHand(interactionHand)
            val item = stack?.item
            val itemId = Registries.ITEM.getId(item)
            if (NaturalMaterials.isNaturalMaterial(itemId)) {
                organicMaterialInside += NaturalMaterials.getContent(itemId) ?: 0
                if (!player.isCreative) {
                    stack?.decrement(1)
                }
                return ActionResult.PASS
            }
        }

        return ActionResult.PASS

    }

    override fun tick() {
        if (timeRemaining > 0) {
            timeRemaining--
        }
    }

    companion object {
        val TICKER = BlockEntityTicker<FossilMultiblockEntity> { _, _, _, blockEntity ->
            if (blockEntity.multiblockStructure != null) {
                blockEntity.multiblockStructure!!.tick()
            }
        }
    }


}
