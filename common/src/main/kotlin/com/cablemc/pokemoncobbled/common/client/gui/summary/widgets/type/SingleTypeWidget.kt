package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.type

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.drawCenteredText
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.mojang.blaze3d.vertex.MatrixStack
import net.minecraft.network.chat.LiteralText

class SingleTypeWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val type: ElementalType
) : TypeWidget(pX, pY, pWidth, pHeight, LiteralText("SingleTypeWidget - ${type.name}")) {

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        pMatrixStack.push()
        pMatrixStack.translate(0.35, 0.0, 0.0)
        renderType(type, pMatrixStack)
        pMatrixStack.pop()
        // Render Type Name
        pMatrixStack.push()
        pMatrixStack.scale(0.35F, 0.35F, 0.35F)
        drawCenteredText(
            poseStack = pMatrixStack, font = CobbledResources.NOTO_SANS_BOLD,
            text = type.displayName,
            x = (x + 34) / 0.35F, y = y / 0.35F + 5.75,
            colour = ColourLibrary.WHITE, shadow = false
        )
        pMatrixStack.pop()
    }
}