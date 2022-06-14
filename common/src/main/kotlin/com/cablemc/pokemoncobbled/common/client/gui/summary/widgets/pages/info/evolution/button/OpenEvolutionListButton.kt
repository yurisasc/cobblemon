package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info.evolution.button

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.util.math.MatrixStack

class OpenEvolutionListButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    clickAction: PressAction,
    private val pokemon: Pokemon
): TexturedButtonWidget(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, RESOURCE, pWidth, pHeight, clickAction) {

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double) = false

    override fun renderButton(poseStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Render Button Image
        blitk(
            matrixStack = poseStack,
            texture = RESOURCE,
            x = x, y = y,
            width = width, height = height
        )

        poseStack.push()
        val scale = 0.4F
        poseStack.scale(scale, scale, 1F)
        // Draw Text
        com.cablemc.pokemoncobbled.common.api.gui.drawCenteredText(
            poseStack = poseStack,
            font = CobbledResources.NOTO_SANS_BOLD,
            text = "pokemoncobbled.ui.evolve".asTranslated(),
            x = (x + BUTTON_WIDTH / 2) / scale, y = (y + 4) / scale,
            colour = if (isHovered) ColourLibrary.BUTTON_HOVER_COLOUR else ColourLibrary.WHITE,
            shadow = false
        )

        poseStack.pop()
    }

    companion object {
        const val BUTTON_WIDTH = 28
        const val BUTTON_HEIGHT = 14
        private val RESOURCE = cobbledResource("ui/summary/summary_button.png")
    }

}