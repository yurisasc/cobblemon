package com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages

import com.cablemc.pokemoncobbled.client.CobbledResources
import com.cablemc.pokemoncobbled.client.gui.ColourLibrary
import com.cablemc.pokemoncobbled.client.gui.drawCenteredText
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component

class SummarySwitchButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val component: Component,
    onPress: OnPress
): Button(pX, pY, pWidth, pHeight, component, onPress) {

    companion object {
        private const val SCALE = 0.75F
    }

    override fun renderButton(pPoseStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        pPoseStack.pushPose()
        pPoseStack.scale(SCALE, SCALE, SCALE)
        if(isHovered())
            drawCenteredText(
                poseStack = pPoseStack,
                font = CobbledResources.NOTO_SANS_BOLD,
                text = component,
                x = (x + width / 2) / SCALE - 0.1, y = y / SCALE,
                colour = ColourLibrary.BUTTON_HOVER_COLOUR
            )
        else
            drawCenteredText(
                poseStack = pPoseStack,
                font = CobbledResources.NOTO_SANS_BOLD,
                text = component,
                x = (x + width / 2) / SCALE - 0.1, y = y / SCALE,
                colour = ColourLibrary.BUTTON_NORMAL_COLOUR
            )
        pPoseStack.popPose()
    }
}