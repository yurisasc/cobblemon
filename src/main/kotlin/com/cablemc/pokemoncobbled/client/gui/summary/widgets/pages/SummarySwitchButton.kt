package com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component

class SummarySwitchButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val component: Component,
    onPress: OnPress
): Button(pX, pY, pWidth, pHeight, component, onPress) {

    companion object {
        private const val HOVER_COLOUR = 0xB5C42F
        private const val NORMAL_COLOUR = 0xFFFFFF
    }

    override fun renderButton(pPoseStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        if(isHovered())
            GuiComponent.drawCenteredString(pPoseStack, Minecraft.getInstance().font, component, x + width / 2 , y + 4, HOVER_COLOUR)
        else
            GuiComponent.drawCenteredString(pPoseStack, Minecraft.getInstance().font, component, x + width / 2 , y + 4, NORMAL_COLOUR)
    }
}