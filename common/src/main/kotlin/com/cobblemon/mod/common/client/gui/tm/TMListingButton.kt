package com.cobblemon.mod.common.client.gui.tm

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.tms.TechnicalMachine
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.client.render.renderScaledGuiItemIcon
import com.cobblemon.mod.common.item.components.TMMoveComponent
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

class TMListingButton(
    pX: Int, pY: Int,
    onPress: PressAction,
    val tm: TechnicalMachine
): ButtonWidget(pX, pY, WIDTH.toInt(), HEIGHT.toInt(), Text.literal("All Types"), onPress, DEFAULT_NARRATION_SUPPLIER) {

    companion object {
        private const val HEIGHT = 20F
        private const val WIDTH = 140F
        val TM_LISTING_BUTTON = cobblemonResource("textures/gui/tm/tm_selection_listing.png")
    }

    val stack = TMMoveComponent.createStack(tm.move)

    override fun renderWidget(context: DrawContext, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        blitk(
            matrixStack = context.matrices,
            texture = TM_LISTING_BUTTON,
            x = x,
            y = y,
            width = WIDTH,
            height = HEIGHT,
            vOffset = if (isHovered(pMouseX.toDouble(), pMouseY.toDouble())) HEIGHT else 0,
            textureHeight = HEIGHT * 2
        )

        renderScaledGuiItemIcon(
            itemStack = stack,
            x = x.toDouble() + 2,
            y = y.toDouble() + 2
        )

        drawScaledText(
            context = context,
            text = tm.translatedMoveName(),
            x = x + 25,
            y = y + 7.5,
            scale = 0.75f
        )
    }

    override fun playDownSound(soundManager: SoundManager) {}
    fun isHovered(mouseX: Double, mouseY: Double) = mouseX.toFloat() in (x.toFloat()..(x.toFloat() + WIDTH)) && mouseY.toFloat() in (y.toFloat()..(y.toFloat() + HEIGHT))


}