package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages

import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.drawCenteredText
import com.mojang.blaze3d.vertex.MatrixStack
import net.minecraft.client.gui.components.Button
import net.minecraft.text.Text

class SummarySwitchButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val component: Text,
    onPress: OnPress
): Button(pX, pY, pWidth, pHeight, component, onPress) {

    companion object {
        private const val SCALE = 0.75F
    }

    override fun renderButton(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        pMatrixStack.push()
        pMatrixStack.scale(SCALE, SCALE, SCALE)
        if (isHovered)
            drawCenteredText(
                poseStack = pMatrixStack,
                font = CobbledResources.NOTO_SANS_BOLD,
                text = component,
                x = (x + width / 2) / SCALE - 0.1, y = y / SCALE,
                colour = ColourLibrary.BUTTON_HOVER_COLOUR
            )
        else
            drawCenteredText(
                poseStack = pMatrixStack,
                font = CobbledResources.NOTO_SANS_BOLD,
                text = component,
                x = (x + width / 2) / SCALE - 0.1, y = y / SCALE,
                colour = ColourLibrary.BUTTON_NORMAL_COLOUR
            )
        pMatrixStack.pop()
    }
}