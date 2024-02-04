package com.cobblemon.mod.common.sherds

import net.minecraft.item.Item
import net.minecraft.util.Identifier

data class CobblemonSherd(
    val patternId: Identifier,
    val item: Item
) {
}