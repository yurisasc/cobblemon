package com.cobblemon.mod.common.client.tooltips

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

abstract class TooltipGenerator {
    open fun generateTooltip(stack: ItemStack, lines: MutableList<Component>): MutableList<Component>? {
        return null
    }
    open fun generateAdditionalTooltip(stack: ItemStack, lines: MutableList<Component>): MutableList<Component>? {
        return null
    }
}