package com.cobblemon.mod.common.gui

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType

class TMMScreenHandler(syncId: Int) : ScreenHandler(CobblemonScreenHandlers.TMM_SCREEN, syncId) {
    var playerEntity: PlayerEntity? = null
    var playerInventory: PlayerInventory? = null
    constructor(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?) : this(syncId, playerInventory) {
        playerEntity = player

    }
    constructor(syncId: Int, playerInventory: PlayerInventory) : this(syncId) {
        this.playerInventory = playerInventory
    }
    override fun quickMove(player: PlayerEntity?, slot: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        return true
    }

}