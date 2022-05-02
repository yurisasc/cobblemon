package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.type

import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class DualTypeWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pMessage: Text,
    private val mainType: ElementalType, private val secondaryType: ElementalType
) : TypeWidget(pX, pY, pWidth, pHeight, pMessage) {

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        renderType(mainType, secondaryType, pMatrixStack)
    }
}