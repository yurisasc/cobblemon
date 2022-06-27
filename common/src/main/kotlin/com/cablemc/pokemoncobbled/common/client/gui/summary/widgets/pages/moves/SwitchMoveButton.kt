package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.gui.drawCenteredText
import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.lang
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.util.math.MatrixStack

class SwitchMoveButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    var move: MoveTemplate,
    var movesWidget: MovesWidget,
    onPress: PressAction
): TexturedButtonWidget(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, switchMoveButtonResource, pWidth, pHeight, onPress) {

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean {
        return false
    }

    companion object {
        const val SWITCH_MOVE_BUTTON_WIDTH = 28
        const val SWITCH_MOVE_BUTTON_HEIGHT = 14
        private val switchMoveButtonResource = cobbledResource("ui/summary/summary_moves_change_button.png")
    }

    override fun renderButton(poseStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Render Button Image
        blitk(
            matrixStack = poseStack,
            texture = switchMoveButtonResource,
            x = x, y = y,
            width = width, height = height
        )

        poseStack.push()
        val scale = 0.4F
        poseStack.scale(scale, scale, 1F)
        // Draw Text
        drawCenteredText(
            poseStack = poseStack,
            font = CobbledResources.NOTO_SANS_BOLD,
            text = lang("ui.changemove"),
            x = (x + SWITCH_MOVE_BUTTON_WIDTH / 2) / scale, y = (y + 4) / scale,
            colour = if (isHovered || movesWidget.moveSwitchPane?.replacedMove?.template == move) ColourLibrary.BUTTON_HOVER_COLOUR else ColourLibrary.WHITE,
            shadow = false
        )

        poseStack.pop()
    }
}