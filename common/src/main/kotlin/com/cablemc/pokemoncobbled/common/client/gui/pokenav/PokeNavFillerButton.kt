package com.cablemc.pokemoncobbled.common.client.gui.pokenav

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText

class PokeNavFillerButton(
    posX: Int, posY: Int,
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    pTextureWidth: Int, pTextureHeight: Int
): PokeNavImageButton(posX, posY, pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, FILLER, pTextureWidth, pTextureHeight, {}, LiteralText.EMPTY) {

    override fun renderButton(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        this.applyBlitk(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
        pMatrixStack.push()
    }

    override fun applyBlitk(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        blitk(
            matrixStack = pMatrixStack,
            texture = FILLER,
            x = x, y = y + 0.25,
            width = width, height = height,
            red = RED,
            green = GREEN,
            blue = BLUE,
            alpha = ALPHA
        )
    }

    companion object {

        const val RED = .28235
        const val GREEN = .29412
        const val BLUE = .30980
        const val ALPHA = .9

        private val FILLER = cobbledResource("ui/pokenav/pokenav_filler.png")

    }

}