package com.cobblemon.mod.common.gui

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType

class TMMScreenHandler(syncId: Int) : ScreenHandler(CobblemonScreenHandlers.TMM_SCREEN, syncId) {
    var playerInventory: PlayerInventory? = null

    val input: RecipeInputInventory = CraftingInventory(this, 3, 1)
    val result: CraftingResultInventory = CraftingResultInventory()
    private var inventory: Inventory? = null

    constructor(syncId: Int, playerInventory: PlayerInventory) : this(syncId, playerInventory, SimpleInventory(4)) {
    }
    constructor(syncId: Int, playerInventory: PlayerInventory, inventory: Inventory) : this(syncId) {
        val startX = 0 - 9
        val startY = 112
        val xLen = 18 // 18 is standard
        val yLen = 18 // 18 is standard
        this.playerInventory = playerInventory
        this.inventory = inventory
        for (row in 0..2) {
            for (column in 0..8) {
                this.addSlot(Slot(playerInventory, 9 + (row * 9) + column, startX + (xLen * column), startY + (yLen * row)))
            }
        }
        for (column in 0..8) {
            this.addSlot(Slot(playerInventory, column, startX + (xLen * column), startY + 58))
        }

        this.addSlot(CraftingResultSlot(
            playerInventory.player,
            input,
            this.inventory,
            3,
            startX + 123,
            startY - 22
        ))

        this.addSlot(Slot(inventory, 0, startX + 167, startY + 9))  // material input slot 1
        this.addSlot(Slot(inventory, 1, startX + 185, startY + 9))  // material input slot 2
        this.addSlot(Slot(inventory, 2, startX + 203, startY + 9))  // material input slot 3

    }
    override fun quickMove(player: PlayerEntity, slot: Int): ItemStack {
        return if (insertItem(getSlot(slot).stack, 0, SLOT_COUNT, true)) getSlot(slot).stack else ItemStack.EMPTY
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        return inventory?.canPlayerUse(player) ?: false
    }

//    override fun onClosed(player: PlayerEntity) {
////        for (i in 0..2) {
////            if (!slots[i].stack.isEmpty) player.giveOrDropItemStack(slots[i].stack, false)
////        }
//        super.onClosed(player)
//    }

    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, player: PlayerEntity) {
        val type = if (actionType == SlotActionType.THROW) SlotActionType.PICKUP else actionType
        super.onSlotClick(slotIndex, button, type, player)
    }

    companion object {
        val SLOT_COUNT = 3

    }

}