package com.cobblemon.mod.common.gui

import com.cobblemon.mod.common.client.gui.tm.TMMHandledScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType

class TMMScreenHandler(syncId: Int) : ScreenHandler(CobblemonScreenHandlers.TMM_SCREEN, syncId) {
    var playerEntity: PlayerEntity? = null
    var playerInventory: PlayerInventory? = null
    constructor(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?) : this(syncId, playerInventory) {
        playerEntity = player
    }
    constructor(syncId: Int, playerInventory: PlayerInventory) : this(syncId) {
        val startX = 0 - 9
        val startY = 112
        val xLen = 18 // 18 is standard
        val yLen = 18 // 18 is standard
        this.playerInventory = playerInventory
        val tmInventory = CraftingInventory(this, TMMHandledScreen.TEXTURE_WIDTH, TMMHandledScreen.TEXTURE_HEIGHT)
        for (row in 0..2) {
            for (column in 0..8) {
                this.addSlot(Slot(playerInventory, 9 + (row * 9) + column, startX + (xLen * column), startY + (yLen * row)))
            }
        }
        for (column in 0..8) {
            this.addSlot(Slot(playerInventory, column, startX + (xLen * column), startY + 58))
        }

        this.addSlot(Slot(tmInventory, 36, startX + 122, startY - 22)) // output slot
        this.addSlot(Slot(tmInventory, 37, startX + 167, startY + 9))  // material input slot 1
        this.addSlot(Slot(tmInventory, 38, startX + 185, startY + 9))  // material input slot 2
        this.addSlot(Slot(tmInventory, 39, startX + 203, startY + 9))  // material input slot 3

    }
    override fun quickMove(player: PlayerEntity, slot: Int): ItemStack {
        return if (insertItem(getSlot(slot).stack, 0, SLOT_COUNT, true)) getSlot(slot).stack else ItemStack.EMPTY
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        return true
    }

    companion object {
        val SLOT_COUNT = 40
    }

}