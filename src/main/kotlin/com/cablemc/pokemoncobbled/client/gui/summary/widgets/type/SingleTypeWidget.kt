package com.cablemc.pokemoncobbled.client.gui.summary.widgets.type

import com.cablemc.pokemoncobbled.client.gui.ColourLibrary
import com.cablemc.pokemoncobbled.client.gui.Fonts
import com.cablemc.pokemoncobbled.client.gui.drawCenteredText
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.chat.TextComponent

class SingleTypeWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val type: ElementalType
) : TypeWidget(pX, pY, pWidth, pHeight, TextComponent("SingleTypeWidget - ${type.name}")) {

    override fun render(pPoseStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        renderType(type, pPoseStack)
        // Render Type Name
        pPoseStack.pushPose()
        pPoseStack.scale(0.4F, 0.4F, 0.4F)
        drawCenteredText(
            poseStack = pPoseStack, font = Fonts.OSWALD,
            text = type.displayName,
            x = (x + 30) / 0.4F, y = y / 0.4F + 3,
            colour = ColourLibrary.WHITE, shadow = false
        )
        pPoseStack.popPose()
    }
}