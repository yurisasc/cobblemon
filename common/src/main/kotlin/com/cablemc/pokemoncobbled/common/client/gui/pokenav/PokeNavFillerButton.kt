package com.cablemc.pokemoncobbled.common.client.gui.pokenav

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.chat.TextComponent

class PokeNavFillerButton(
    posX: Int, posY: Int,
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    pTextureWidth: Int, pTextureHeight: Int
): PokeNavImageButton(posX, posY, pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, FILLER, pTextureWidth, pTextureHeight, {}, TextComponent.EMPTY) {

    override fun renderButton(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        this.applyBlitk(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
        pMatrixStack.pushPose()
    }

    override fun applyBlitk(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        blitk(
            poseStack = pMatrixStack,
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