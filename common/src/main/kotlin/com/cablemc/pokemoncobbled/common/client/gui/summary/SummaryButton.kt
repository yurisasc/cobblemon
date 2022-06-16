package com.cablemc.pokemoncobbled.common.client.gui.summary

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class SummaryButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    clickAction: PressAction,
    private val text: Text,
    private val resource: Identifier = cobbledResource("ui/summary/summary_button.png"),
    private val renderRequirement: ((button: TexturedButtonWidget) -> Boolean) = { true },
    private val clickRequirement: ((button: TexturedButtonWidget) -> Boolean) = { true },
    private val hoverColorRequirement: ((button: TexturedButtonWidget) -> Boolean) = { button -> button.isHovered }
): TexturedButtonWidget(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, resource, pWidth, pHeight, clickAction) {

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double) = false

    override fun renderButton(poseStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        if (!this.renderRequirement.invoke(this)) {
            return
        }
        // Render Button Image
        blitk(
            matrixStack = poseStack,
            texture = this.resource,
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
            text = this.text,
            x = (x + BUTTON_WIDTH / 2) / scale, y = (y + 4) / scale,
            colour = if (this.hoverColorRequirement.invoke(this)) ColourLibrary.BUTTON_HOVER_COLOUR else ColourLibrary.WHITE,
            shadow = false
        )

        poseStack.pop()
    }

    override fun onPress() {
        if (this.clickRequirement.invoke(this)) {
            super.onPress()
        }
    }

    companion object {

        const val BUTTON_WIDTH = 28
        const val BUTTON_HEIGHT = 14

    }

}