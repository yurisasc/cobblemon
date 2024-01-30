package com.cobblemon.mod.common.gui

import com.cobblemon.mod.common.block.entity.TMBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TMMScreenHandler(syncId: Int) : ScreenHandler(CobblemonScreenHandlers.TMM_SCREEN, syncId) {
    var playerInventory: PlayerInventory? = null

    var input: RecipeInputInventory = CraftingInventory(this, 3, 1)
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
        this.inventory = (inventory)// as TMBlockEntity.TMBlockInventory) // this does grab the right inventory slots

        if (inventory is TMBlockEntity.TMBlockInventory) {
            //inventory.tmBlockEntity

            // sync the input slots with the TMM slots
            input.setStack(0, inventory.items!!.get(0))
            input.setStack(1, inventory.items!!.get(1))
            input.setStack(2, inventory.items!!.get(2))
            //inventory.items!![3] = result.getStack(0) // sync output of TMM with the crafted TM
            result.setStack(0, inventory.items!!.get(3))
        }


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

        this.addSlot(Slot(this.inventory, 0, startX + 167, startY + 9))  // material input slot 1
        this.addSlot(Slot(this.inventory, 1, startX + 185, startY + 9))  // material input slot 2
        this.addSlot(Slot(this.inventory, 2, startX + 203, startY + 9))  // material input slot 3

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
        val type = if (actionType == SlotActionType.THROW) SlotActionType.PICKUP else actionType // todo issue might be inventory is not being clicked, but it is index 36 still
        super.onSlotClick(slotIndex, button, type, player)
    }

    override fun syncState() {
        if (inventory is TMBlockEntity.TMBlockInventory) {
            //inventory.tmBlockEntity

            // sync the input slots with the TMM slots
            input.setStack(0, (inventory as TMBlockEntity.TMBlockInventory).items!!.get(0))
            input.setStack(1, (inventory as TMBlockEntity.TMBlockInventory).items!!.get(1))
            input.setStack(2, (inventory as TMBlockEntity.TMBlockInventory).items!!.get(2))
            //inventory.items!![3] = result.getStack(0) // sync output of TMM with the crafted TM
            result.setStack(0, (inventory as TMBlockEntity.TMBlockInventory).items!!.get(3))
        }

        super.syncState()
    }

    companion object {
        val SLOT_COUNT = 3
    }

}