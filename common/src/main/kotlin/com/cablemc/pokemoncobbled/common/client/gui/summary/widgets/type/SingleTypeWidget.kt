package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.type

import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.drawCenteredText
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
        pPoseStack.scale(0.35F, 0.35F, 0.35F)
        drawCenteredText(
            poseStack = pPoseStack, font = CobbledResources.NOTO_SANS_BOLD,
            text = type.displayName,
            x = (x + 34) / 0.35F, y = y / 0.35F + 5.75,
            colour = ColourLibrary.WHITE, shadow = false
        )
        pPoseStack.popPose()
    }
}