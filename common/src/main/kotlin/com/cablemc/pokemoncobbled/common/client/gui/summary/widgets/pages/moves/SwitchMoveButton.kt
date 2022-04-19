package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.gui.drawCenteredText
import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.ImageButton

class SwitchMoveButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    var move: MoveTemplate,
    var movesWidget: MovesWidget,
    onPress: OnPress
): ImageButton(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, switchMoveButtonResource, pWidth, pHeight, onPress) {

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean {
        return false
    }

    companion object {
        const val SWITCH_MOVE_BUTTON_WIDTH = 28
        const val SWITCH_MOVE_BUTTON_HEIGHT = 14
        private val switchMoveButtonResource = cobbledResource("ui/summary/summary_moves_change_button.png")
    }

    override fun renderButton(poseStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Render Button Image
        blitk(
            poseStack = poseStack,
            texture = switchMoveButtonResource,
            x = x, y = y,
            width = width, height = height
        )

        poseStack.pushPose()
        val scale = 0.4F
        poseStack.scale(scale, scale, 1F)
        // Draw Text
        drawCenteredText(
            poseStack = poseStack,
            font = CobbledResources.NOTO_SANS_BOLD,
            text = "pokemoncobbled.ui.changemove".asTranslated(),
            x = (x + SWITCH_MOVE_BUTTON_WIDTH / 2) / scale, y = (y + 4) / scale,
            colour = if (isHovered || movesWidget.moveSwitchPane?.replacedMove?.template == move) ColourLibrary.BUTTON_HOVER_COLOUR else ColourLibrary.WHITE,
            shadow = false
        )

        poseStack.popPose()
    }
}