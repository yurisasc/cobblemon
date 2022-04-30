package com.cablemc.pokemoncobbled.common.client.gui.summary

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.util.math.MatrixStack

class ExitButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    onPress: PressAction
): TexturedButtonWidget(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, exitButtonResource, onPress) {

    companion object {
        private const val EXIT_BUTTON_WIDTH = 21.25F
        private const val EXIT_BUTTON_HEIGHT = 15F
        private val exitButtonResource = cobbledResource("ui/summary/summary_overlay_exit.png")
    }

    override fun renderButton(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        hovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height
        if (isHovered) {
            blitk(
                matrixStack = pMatrixStack,
                x = x + 1.75F, y = y - 0.5F,
                texture = exitButtonResource,
                width = EXIT_BUTTON_WIDTH, height = EXIT_BUTTON_HEIGHT
            )
        }
    }

}