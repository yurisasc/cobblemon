package com.cobblemon.mod.common.client.gui.tm

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text

class DiscButton(
    pX: Int, pY: Int,
    onPress: PressAction,
): ButtonWidget(pX, pY, WIDTH.toInt(), HEIGHT.toInt(), Text.literal("All Types"), onPress, DEFAULT_NARRATION_SUPPLIER) {

    companion object {
        private const val WIDTH = 53F
        private const val HEIGHT = 53F
        val DISC_BUTTON = cobblemonResource("textures/gui/tm/disc_button.png")
    }

    override fun renderButton(context: DrawContext, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        blitk(
            matrixStack = context.matrices,
            texture = DISC_BUTTON,
            x = x,
            y = y,
            width = WIDTH,
            height = HEIGHT,
            vOffset = if (isHovered(pMouseX.toDouble(), pMouseY.toDouble())) HEIGHT else 0,
            textureHeight = HEIGHT * 2
        )
    }

    override fun playDownSound(soundManager: SoundManager) {}
    fun isHovered(mouseX: Double, mouseY: Double) = mouseX.toFloat() in (x.toFloat()..(x.toFloat() + WIDTH)) && mouseY.toFloat() in (y.toFloat()..(y.toFloat() + HEIGHT))

}