package com.cobblemon.mod.common.gui

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType

class TMMScreenHandler(syncId: Int) : ScreenHandler(CobblemonScreenHandlers.TMM_SCREEN, syncId) {
    override fun quickMove(player: PlayerEntity?, slot: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        TODO("Not yet implemented")
    }

    object TMMScreenHandlerFactory : ScreenHandlerType.Factory<TMMScreenHandler> {
        override fun create(syncId: Int, playerInventory: PlayerInventory?): TMMScreenHandler {
            return TMMScreenHandler(syncId)
        }

    }

}