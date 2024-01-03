package com.cobblemon.mod.common.gui

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot

class TMMScreenHandler(syncId: Int) : ScreenHandler(CobblemonScreenHandlers.TMM_SCREEN, syncId) {
    var playerEntity: PlayerEntity? = null
    var playerInventory: PlayerInventory? = null
    constructor(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?) : this(syncId, playerInventory) {
        playerEntity = player
    }
    constructor(syncId: Int, playerInventory: PlayerInventory) : this(syncId) {
        val startX = 1
        val startY = 143
        val xLen = 18
        val yLen = 18
        this.playerInventory = playerInventory
        for (row in 0..2) {
            for (column in 0..8) {
                this.addSlot(Slot(playerInventory, 9 + (row * 9) + column, startX + (xLen * column), startY +  (yLen * row)))
            }
        }
        for (column in 0..8) {
            this.addSlot(Slot(playerInventory, column, startX + (xLen * column), startY + 58))
        }

    }
    override fun quickMove(player: PlayerEntity?, slot: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        return true
    }

}