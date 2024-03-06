package com.cobblemon.mod.common.client.gui.modstation

import com.cobblemon.mod.common.gui.ModStationScreenHandler
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerListener
import net.minecraft.text.Text

class ModStationHandledScreen(
    val handler: ModStationScreenHandler,
    val inventory: PlayerInventory,
    title: Text?
) : HandledScreen<ModStationScreenHandler>(handler, inventory, title), ScreenHandlerListener {
    override fun drawBackground(context: DrawContext?, delta: Float, mouseX: Int, mouseY: Int) {

    }

    override fun onSlotUpdate(handler: ScreenHandler?, slotId: Int, stack: ItemStack?) {

    }

    override fun onPropertyUpdate(handler: ScreenHandler?, property: Int, value: Int) {

    }
}