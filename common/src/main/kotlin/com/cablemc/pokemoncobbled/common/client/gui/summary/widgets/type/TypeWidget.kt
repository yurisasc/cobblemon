package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.type

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

abstract class TypeWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pMessage: Text
): SoundlessWidget(pX, pY, pWidth, pHeight, pMessage) {

    companion object {
        val typeResource = cobbledResource("ui/types.png")
        private const val OFFSET = 0.5
    }

    fun renderType(type: ElementalType, pMatrixStack: MatrixStack, pX: Int = x, pY: Int = y) {
        blitk(
            matrixStack = pMatrixStack,
            texture = typeResource,
            x = pX + OFFSET, y = pY,
            width = width, height = height,
            uOffset = width * type.textureXMultiplier.toFloat() + 0.1,
            textureWidth = width * 18
        )
    }

    fun renderType(mainType: ElementalType, secondaryType: ElementalType, pMatrixStack: MatrixStack) {
        renderType(secondaryType, pMatrixStack, x + 16)
        renderType(mainType, pMatrixStack)
    }
}