package com.cobblemon.mod.common.item.group

import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier

interface ItemGroupProvider {

    val identifier: Identifier

    val displayName: Text

    val icon: () -> ItemStack

    fun group(): ItemGroup

    fun assign(group: ItemGroup)

}