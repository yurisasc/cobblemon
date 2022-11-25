package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.api.text.gray
import com.cobblemon.mod.common.util.tooltipLang
import net.minecraft.block.ComposterBlock
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World

class BerryItem(private val name: String) : CobblemonItem(Settings().group(CobblemonItemGroups.PLANTS)) {

    init {
        // 65% to raise composter level
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE[this] = .65F
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip.add(tooltipLang(this.name).gray())
    }

}