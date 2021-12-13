package com.cablemc.pokemoncobbled.client.gui.summary.widgets.type

import com.cablemc.pokemoncobbled.client.gui.summary.mock.ElementalType
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.chat.TextComponent

class SingleTypeWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val type: ElementalType
) : TypeWidget(pX, pY, pWidth, pHeight, TextComponent("SingleTypeWidget - ${type.name}")) {
    override fun render(pPoseStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        renderType(type, pPoseStack)
    }
}