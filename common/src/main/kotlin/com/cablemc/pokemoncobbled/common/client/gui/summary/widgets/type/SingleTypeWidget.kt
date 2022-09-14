package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.type

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.client.render.drawScaledText
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class SingleTypeWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val type: ElementalType,
    private val renderText: Boolean = true
) : TypeWidget(pX, pY, pWidth, pHeight, Text.literal("SingleTypeWidget - ${type.name}")) {

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        pMatrixStack.push()
        pMatrixStack.translate(0.35, 0.0, 0.0)
        renderType(type, pMatrixStack)
        pMatrixStack.pop()
        // Render Type Name
        if (this.renderText) {
            pMatrixStack.push()
            drawScaledText(
                matrixStack = pMatrixStack,
                text = type.displayName,
                x = x + 35.5F, y = y + 3F,
                colour = ColourLibrary.WHITE, shadow = false,
                centered = true,
                maxCharacterWidth = 40,
                scale = 0.6F
            )
        }
    }
}