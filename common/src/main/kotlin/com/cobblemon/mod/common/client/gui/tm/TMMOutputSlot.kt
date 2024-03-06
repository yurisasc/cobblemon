package com.cobblemon.mod.common.client.gui.tm

import com.cobblemon.mod.common.gui.TMMScreenHandler
import com.cobblemon.mod.common.util.giveOrDropItemStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.slot.Slot

class TMMOutputSlot(val screen: TMMScreenHandler, index: Int, x: Int, y: Int) : Slot(screen.playerInventory, index, x, y) {

    override fun canInsert(stack: ItemStack?) = false

    override fun onTakeItem(player: PlayerEntity, stack: ItemStack) {
        player.giveOrDropItemStack(stack, false)
        this.stack = Items.AIR.defaultStack
    }

}