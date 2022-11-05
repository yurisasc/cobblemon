/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.summary

import com.cobblemon.mod.common.api.gui.ColourLibrary
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

class SummaryButton(
    var buttonX: Float,
    var buttonY: Float,
    val buttonWidth: Number,
    val buttonHeight: Number,
    val clickAction: PressAction,
    private val text: MutableText,
    private val resource: Identifier = cobblemonResource("ui/summary/summary_button.png"),
    private val renderRequirement: ((button: SummaryButton) -> Boolean) = { true },
    private val clickRequirement: ((button: SummaryButton) -> Boolean) = { true },
    private val silent: Boolean = false,
    private val textScale: Float = 1F
): ButtonWidget(buttonX.toInt(), buttonY.toInt(), buttonWidth.toInt(), buttonHeight.toInt(), text, clickAction) {

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double) = false
    override fun appendNarrations(builder: NarrationMessageBuilder) {
    }

    override fun renderButton(poseStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        if (!this.renderRequirement.invoke(this)) {
            return
        }
        // Render Button Image
        blitk(
            matrixStack = poseStack,
            texture = this.resource,
            x = buttonX, y = buttonY,
            width = buttonWidth, height = buttonHeight
        )
        // Draw Text
        drawScaledText(
            matrixStack = poseStack,
            font = null,
            text = this.text,
            scale = textScale,
            x = this.buttonX + buttonWidth.toFloat() / 2,
            y = buttonY + buttonHeight.toFloat() / 2 - textScale / 2 * 8,
            colour = if (isHovered) ColourLibrary.BUTTON_HOVER_COLOUR else ColourLibrary.WHITE,
            shadow = false,
            maxCharacterWidth = (buttonWidth.toFloat() * 0.8F).toInt(),
            centered = true
        )
    }

//    fun isHovered(mouseX: Int, mouseY: Int) = mouseX in x..(x + buttonWidth.toInt()) && mouseY in y..(y + buttonHeight.toInt())

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (this.clickRequirement.invoke(this)) {
            super.mouseClicked(mouseX, mouseY, button)
        }
        return false
    }

    override fun playDownSound(soundManager: SoundManager?) {
        if (!this.silent) {
            super.playDownSound(soundManager)
        }
    }

//    override fun onPress() {
//        this.clickAction()
//    }

    fun setPosFloat(x: Float, y: Float) {
        this.x = x.toInt()
        this.y = y.toInt()
        this.buttonX = x
        this.buttonY = y
    }

    companion object {
        const val BUTTON_WIDTH = 28
        const val BUTTON_HEIGHT = 14
    }
}