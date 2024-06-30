package com.cobblemon.mod.common.client.gui.tm

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text

class EjectButton(
    pX: Int, pY: Int,
    val small: Boolean = false,
    onPress: PressAction
): ButtonWidget(
    pX,
    pY,
    if (small) SMALL_WIDTH.toInt() else LARGE_WIDTH.toInt(),
    if (small) SMALL_HEIGHT.toInt() else LARGE_HEIGHT.toInt(),
    Text.literal("Eject"),
    onPress,
    DEFAULT_NARRATION_SUPPLIER
) {

    companion object {
        private const val LARGE_WIDTH = 54F
        private const val LARGE_HEIGHT = 14F
        private const val SMALL_WIDTH = 14F
        private const val SMALL_HEIGHT = 14F
        val SMALL_BUTTON = cobblemonResource("textures/gui/tm/eject_button_small.png")
        val LARGE_BUTTON = cobblemonResource("textures/gui/tm/eject_button_large.png")
    }

    val height = if (small) SMALL_HEIGHT else LARGE_HEIGHT
    val width = if (small) SMALL_WIDTH else LARGE_WIDTH
    val texture = if (small) SMALL_BUTTON else LARGE_BUTTON

    override fun renderWidget(context: DrawContext, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        blitk(
            matrixStack = context.matrices,
            texture = texture,
            x = x,
            y = y,
            width = width,
            height = height,
            vOffset = if (isHovered(pMouseX.toDouble(), pMouseY.toDouble())) height else 0,
            textureHeight = height * 2
        )
    }

    override fun playDownSound(soundManager: SoundManager) {}
    fun isHovered(mouseX: Double, mouseY: Double) = mouseX.toFloat() in (x.toFloat()..(x.toFloat() + width)) && mouseY.toFloat() in (y.toFloat()..(y.toFloat() + height))


}