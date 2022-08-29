package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.text.bold
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.render.drawScaledText
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText

class SummarySwitchButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    component: MutableText,
    onPress: PressAction
): ButtonWidget(pX, pY, pWidth, pHeight, component, onPress) {
    val text = component.bold()

    override fun renderButton(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        pMatrixStack.push()
        // It was rendering behind other shit. There's a better way to fix this but fuck it
        pMatrixStack.translate(0.0, 0.0, 10.0)
        if (isHovered)
            drawScaledText(
                matrixStack = pMatrixStack,
                font = CobbledResources.DEFAULT_LARGE,
                text = text,
                scale = 1.3F,
                x = x + width / 2 - 0.1, y = y + 1,
                colour = ColourLibrary.BUTTON_HOVER_COLOUR,
                centered = true
            )
        else
            drawScaledText(
                matrixStack = pMatrixStack,
                font = CobbledResources.DEFAULT_LARGE,
                text = text,
                scale = 1.3F,
                x = x + width / 2 - 0.1, y = y + 1,
                colour = ColourLibrary.BUTTON_NORMAL_COLOUR,
                centered = true,
                maxCharacterWidth = 36
            )
        pMatrixStack.pop()
    }
}