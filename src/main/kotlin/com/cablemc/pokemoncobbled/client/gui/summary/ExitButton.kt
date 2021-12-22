package com.cablemc.pokemoncobbled.client.gui.summary

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.resources.ResourceLocation

class ExitButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    private val resourceLocation: ResourceLocation, private val pTextureWidth: Int, private val pTextureHeight: Int,
    onPress: OnPress
): ImageButton(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, resourceLocation, pTextureWidth, pTextureHeight, onPress) {

    override fun renderButton(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        isHovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height
        if(isHovered()) {
            RenderSystem.setShaderTexture(0, resourceLocation)
            RenderSystem.enableDepthTest()
            blit(pMatrixStack, x - 296, y - 6, 0F, 0F, pTextureWidth, pTextureHeight, pTextureWidth, pTextureHeight)
        }
    }

}