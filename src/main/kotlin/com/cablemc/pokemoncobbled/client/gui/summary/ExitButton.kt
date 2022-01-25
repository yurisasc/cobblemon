package com.cablemc.pokemoncobbled.client.gui.summary

import com.cablemc.pokemoncobbled.client.gui.blitk
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.ImageButton

class ExitButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    onPress: OnPress
): ImageButton(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, exitButtonResource, onPress) {

    companion object {
        private const val EXIT_BUTTON_WIDTH = 25
        private const val EXIT_BUTTON_HEIGHT = 14.2F
        private val exitButtonResource = cobbledResource("ui/summary/summary_overlay_exit.png")
    }

    override fun renderButton(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        isHovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height
        if(isHovered) {
            blitk(
                poseStack = pMatrixStack,
                x = x + 0.7F, y = y + 1.25F,
                texture = exitButtonResource,
                width = EXIT_BUTTON_WIDTH, height = EXIT_BUTTON_HEIGHT
            )
        }
    }

}