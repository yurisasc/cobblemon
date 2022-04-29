package com.cablemc.pokemoncobbled.common.client.gui.pokenav

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.gui.drawCenteredText
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

open class PokeNavImageButton(
    val posX: Int, val posY: Int,
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    private val resourceLocation: ResourceLocation, pTextureWidth: Int, pTextureHeight: Int,
    onPress: OnPress,
    private val text: Component,
    private val canClick: () -> Boolean = { true }
): ImageButton(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, resourceLocation, pTextureWidth, pTextureHeight, onPress) {

    override fun renderButton(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Render Button Image
        this.applyBlitk(pMatrixStack, pMouseX, pMouseY, pPartialTicks)

        pMatrixStack.pushPose()
        val scale = 0.5F
        pMatrixStack.scale(scale, scale, scale)
        // Draw Text
        drawCenteredText(
            poseStack = pMatrixStack,
            font = CobbledResources.NOTO_SANS_BOLD,
            text = text,
            x = (x + width / 2) / scale, y = (y + height + 3) / scale,
            colour = ColourLibrary.WHITE, shadow = false
        )

        pMatrixStack.popPose()
    }

    fun canClick() = this.canClick.invoke()

    override fun onPress() {
        if (this.canClick())
            super.onPress()
    }
    
    protected open fun applyBlitk(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        blitk(
            poseStack = pMatrixStack,
            texture = resourceLocation,
            x = x, y = y + 0.25,
            width = width, height = height
        )
    }
    
}