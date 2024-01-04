package com.cobblemon.mod.common.client.gui.tm

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.gui.TMMScreenHandler
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerListener
import net.minecraft.text.Text

class TMMHandledScreen(
    handler: TMMScreenHandler?,
    val inventory: PlayerInventory?,
    title: Text?
) : HandledScreen<TMMScreenHandler>(handler, inventory, title), ScreenHandlerListener {

    init {
    }
    override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        super.renderBackground(context)
        val x = (width - TEXTURE_WIDTH) / 2
        val y = (height - TEXTURE_HEIGHT) / 2
        blitk(
            matrixStack = context.matrices,
            texture = TYPE_BASE_TEXTURE,
            x = x, // horizontal placement of GUI
            y = y, // vertical placement of GUI

            width = TEXTURE_WIDTH, // scale of the GUI width
            height = TEXTURE_HEIGHT // scale of the GUI height
        )
    }

    override fun drawForeground(context: DrawContext?, mouseX: Int, mouseY: Int) {
        //Text is usually drawn here, we dont want that
        this.drawMouseoverTooltip(context, mouseX - 158, mouseY - 28)
        //this.renderWithTooltip(context, mouseX, mouseY, 1F)
    }

    override fun onSlotUpdate(handler: ScreenHandler?, slotId: Int, stack: ItemStack?) {

    }

    override fun onPropertyUpdate(handler: ScreenHandler?, property: Int, value: Int) {

    }

    companion object {
        val TEXTURE_HEIGHT = 222
        val TEXTURE_WIDTH = 254
        val TYPE_BASE_TEXTURE = cobblemonResource("textures/gui/tm/tmm_type_selection_base.png")
    }
}