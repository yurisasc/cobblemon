/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemod.common.api.gui.blitk
import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3f

/**
 * This Button is specifically made for the Summary to change the order of the Moves
 *
 * The blocked var was added to prevent the switching the order of Buttons triggering another switch
 */
class MovesMoveButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val isUp: Boolean,
    onPress: PressAction
): ButtonWidget(pX, pY, pWidth, pHeight, Text.literal("MoveButton"), onPress) {

    companion object {
        private const val MOVE_BUTTON_WIDTH = 11
        private const val MOVE_BUTTON_HEIGHT = 8.2F
        private val buttonResource = pokemodResource("ui/summary/summary_moves_overlay_swap_up.png")
        private var blocked = false
    }

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean {
        return false
    }

    override fun renderButton(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        if (isHovered) {
            if (isUp) {
                blitk(
                    matrixStack = pMatrixStack,
                    x = x + 1.5F, y = y + 1.8F,
                    texture = buttonResource,
                    width = MOVE_BUTTON_WIDTH, height = MOVE_BUTTON_HEIGHT
                )
            } else {
                pMatrixStack.push()
                pMatrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0f))
                blitk(
                    matrixStack = pMatrixStack,
                    x = (x + 12.5F) * -1, y = (y + 0.9F + MOVE_BUTTON_HEIGHT) * -1,
                    texture = buttonResource,
                    width = MOVE_BUTTON_WIDTH, height = MOVE_BUTTON_HEIGHT
                )
                pMatrixStack.pop()
            }
        }
    }

    override fun onRelease(pMouseX: Double, pMouseY: Double) {
        blocked = false
    }

    override fun onClick(pMouseX: Double, pMouseY: Double) {
        if (!blocked) {
            blocked = true
            onPress.onPress(this)
        }
    }
}