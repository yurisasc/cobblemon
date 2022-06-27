package com.cablemc.pokemoncobbled.common.client.gui.pokenav

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.gui.drawCenteredText
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier

open class PokeNavImageButton(
    val posX: Int, val posY: Int,
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    private val resourceLocation: Identifier, pTextureWidth: Int, pTextureHeight: Int,
    onPress: PressAction,
    private val text: Text,
    private val canClick: () -> Boolean = { true }
): TexturedButtonWidget(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, resourceLocation, pTextureWidth, pTextureHeight, onPress) {

    override fun renderButton(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Render Button Image
        this.applyBlitk(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
        pMatrixStack.push()
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
        pMatrixStack.pop()
    }

    fun canClick() = this.canClick.invoke()

    override fun onPress() {
        if (this.canClick()) {
            super.onPress()
        }
    }

    override fun playDownSound(soundManager: SoundManager) {
        // Be silent if the button isn't clickable
        if (this.canClick()) {
            super.playDownSound(soundManager)
        }
    }
    
    protected open fun applyBlitk(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        blitk(
            matrixStack = pMatrixStack,
            texture = resourceLocation,
            x = x, y = y + 0.25,
            width = width, height = height
        )
    }
    
}