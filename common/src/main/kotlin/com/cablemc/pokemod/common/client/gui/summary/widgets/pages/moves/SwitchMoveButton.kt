/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemod.common.api.gui.ColourLibrary
import com.cablemc.pokemod.common.api.gui.blitk
import com.cablemc.pokemod.common.api.moves.MoveTemplate
import com.cablemc.pokemod.common.client.render.drawScaledText
import com.cablemc.pokemod.common.util.lang
import com.cablemc.pokemod.common.util.pokemodResource
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
        private val switchMoveButtonResource = pokemodResource("ui/summary/summary_moves_change_button.png")
    }

    override fun renderButton(poseStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Render Button Image
        blitk(
            matrixStack = poseStack,
            texture = switchMoveButtonResource,
            x = x, y = y,
            width = width, height = height
        )

        // Draw Text
        drawScaledText(
            matrixStack = poseStack,
            text = lang("ui.changemove"),
            x = x + SWITCH_MOVE_BUTTON_WIDTH / 2, y = y + 4.5F,
            colour = if (isHovered || movesWidget.moveSwitchPane?.replacedMove?.template == move) ColourLibrary.BUTTON_HOVER_COLOUR else ColourLibrary.WHITE,
            shadow = false,
            centered = true,
            scale = 0.6F,
            maxCharacterWidth = 45
        )
    }
}