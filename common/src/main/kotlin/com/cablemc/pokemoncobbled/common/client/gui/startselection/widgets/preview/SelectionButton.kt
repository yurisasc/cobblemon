package com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.preview

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.gui.drawCenteredText
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText

class SelectionButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    onPress: PressAction
): ButtonWidget(pX, pY, pWidth, pHeight, LiteralText("SelectionButton"), onPress) {

    companion object {
        private val buttonTexture = cobbledResource("ui/starterselection/starterselection_button.png")
        const val BUTTON_WIDTH = 56
        const val BUTTON_HEIGHT = 12
        private const val SCALE = 0.75f
    }

    override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (isHovered)
            blitk(
                matrixStack = matrices,
                texture = buttonTexture,
                x = x + 0.6, y = y + 0.6,
                width = BUTTON_WIDTH - 0.25, height = BUTTON_HEIGHT - 0.25,
                red = 0.75f, green = 0.75f, blue = 0.75f
            )
        else
            blitk(
                matrixStack = matrices,
                texture = buttonTexture,
                x = x + 0.6, y = y + 0.6,
                width = BUTTON_WIDTH - 0.25, height = BUTTON_HEIGHT - 0.25
            )
        matrices.push()
        matrices.scale(SCALE, SCALE, SCALE)
        drawCenteredText(
            poseStack = matrices,
            font = CobbledResources.NOTO_SANS_BOLD_SMALL,
            text = "pokemoncobbled.ui.starter.choosebutton".asTranslated(),
            x = (x + BUTTON_WIDTH / 2) / SCALE, y = (y + BUTTON_HEIGHT / 2 - 2.4) / SCALE,
            colour = ColourLibrary.WHITE,
            shadow = false
        )
        matrices.pop()
    }
}