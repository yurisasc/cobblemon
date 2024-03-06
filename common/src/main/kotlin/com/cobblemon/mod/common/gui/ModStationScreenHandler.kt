package com.cobblemon.mod.common.gui

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.screen.slot.Slot

class ModStationScreenHandler(syncId: Int) : ScreenHandler(CobblemonScreenHandlers.MOD_STATION_SCREEN, syncId) {
    var playerInventory: PlayerInventory? = null
    var input: RecipeInputInventory = CraftingInventory(this, 2, 1)
    val result: CraftingResultInventory = CraftingResultInventory()
    var inventory: Inventory? = null

    constructor(syncId: Int, playerInventory: PlayerInventory) : this(syncId, playerInventory, SimpleInventory(3))

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

        addSlot(CraftingResultSlot(
            playerInventory.player,
            input,
            result,
            0,
            startX,
            startY
        ))

        this.addSlot(Slot(this.inventory, 0, startX, startY - 20))
        this.addSlot(Slot(this.inventory, 1, startX, startY - 40))
    }

    override fun quickMove(player: PlayerEntity?, slot: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        return inventory?.canPlayerUse(player) ?: false
    }

    override fun syncState() {
        super.syncState()
    }
}