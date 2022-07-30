package com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.config.starter.StarterCategory
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.util.math.MatrixStack
import com.cablemc.pokemoncobbled.common.api.gui.drawCenteredText
import com.cablemc.pokemoncobbled.common.client.CobbledResources

class CategoryButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    private val category: StarterCategory,
    onPress: PressAction
): TexturedButtonWidget(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, categoryResource, onPress) {

    companion object {
        private const val CATEGORY_BUTTON_WIDTH = 51.5f
        private const val CATEGORY_BUTTON_HEIGHT = 16f
        private val categoryResource = cobbledResource("ui/starterselection/starterselection_slot.png")
    }

    override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height
        if (true) {
            blitk(
                matrixStack = matrices,
                x = x + 0.5f, y = y,
                texture = categoryResource,
                width = CATEGORY_BUTTON_WIDTH, height = CATEGORY_BUTTON_HEIGHT
            )
        }
        matrices.push()
        matrices.scale(0.8f, 0.95f, 0.95f)
        drawCenteredText(
            poseStack = matrices,
            font = CobbledResources.NOTO_SANS_BOLD_SMALL,
            text = category.displayName,
            x = (x + 25.75f) / 0.8f, y = (y + 2.0f) / 0.95f,
            colour = ColourLibrary.WHITE, shadow = false
        )
        matrices.pop()
    }
}