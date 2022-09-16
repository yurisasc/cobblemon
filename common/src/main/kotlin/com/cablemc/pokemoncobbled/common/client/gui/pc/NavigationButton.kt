/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.gui.pc

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.util.math.MatrixStack

class NavigationButton(
    pX: Int, pY: Int,
    val pWidth: Int, val pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    private val forward: Boolean,
    onPress: PressAction,
): TexturedButtonWidget(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, forwardButtonResource, onPress) {

    companion object {
        private val forwardButtonResource = cobbledResource("ui/pc/pc_box_right.png")
        private val backwardsButtonResource = cobbledResource("ui/pc/pc_box_left.png")
    }

    override fun renderButton(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        hovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height
        if (isHovered) {
            blitk(
                matrixStack = pMatrixStack,
                x = x + 0.25, y = y - 0.5F,
                texture = if (forward) forwardButtonResource else backwardsButtonResource,
                width = pWidth, height = pHeight
            )
        }
    }

}