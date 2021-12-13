package com.cablemc.pokemoncobbled.client.gui.summary

import com.cablemc.pokemoncobbled.client.gui.blitk
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.resources.ResourceLocation

class ExitButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    private val resourceLocation: ResourceLocation, pTextureWidth: Int, pTextureHeight: Int,
    onPress: OnPress
): ImageButton(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, resourceLocation, pTextureWidth, pTextureHeight, onPress) {

    override fun renderButton(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        if(isHovered()) {
            blitk(pMatrixStack, resourceLocation, x, y, height, width)
        }
    }
}