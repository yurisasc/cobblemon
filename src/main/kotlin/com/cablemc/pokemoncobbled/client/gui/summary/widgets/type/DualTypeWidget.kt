package com.cablemc.pokemoncobbled.client.gui.summary.widgets.type

import com.cablemc.pokemoncobbled.client.gui.summary.mock.ElementalType
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.chat.Component

class DualTypeWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pMessage: Component,
    private val mainType: ElementalType, private val secondaryType: ElementalType
) : TypeWidget(pX, pY, pWidth, pHeight, pMessage) {

    override fun render(pPoseStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        renderType(mainType, secondaryType, pPoseStack)
    }
}