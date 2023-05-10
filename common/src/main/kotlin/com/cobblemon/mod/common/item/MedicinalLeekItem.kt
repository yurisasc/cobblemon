package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.block.MedicinalLeekBlock
import net.minecraft.block.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AliasedBlockItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.PlaceableOnWaterItem
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.RaycastContext
import net.minecraft.world.World

class MedicinalLeekItem(block: MedicinalLeekBlock) : AliasedBlockItem(block, Settings()) {

    init {
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE[this] = .65F
    }

    override fun use(world: World?, user: PlayerEntity, hand: Hand?): TypedActionResult<ItemStack?>? {
        val blockHitResult = PlaceableOnWaterItem.raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY)
        val blockHitResult2 = blockHitResult.withBlockPos(blockHitResult.blockPos.up())
        val actionResult: ActionResult = super.useOnBlock(ItemUsageContext(user, hand, blockHitResult2))
        return TypedActionResult(actionResult, user.getStackInHand(hand))
    }

    override fun useOnBlock(context: ItemUsageContext?): ActionResult? {
        return ActionResult.PASS
    }
}