/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.interact.wheel

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class InteractWheelGUI(private val options: Map<Orientation, InteractWheelOption>, title: Text) :
    Screen(title) {

    companion object {
        const val SIZE = 138
        const val OPTION_SIZE = 69
        private val backgroundResource = cobblemonResource("textures/gui/interact/interact_base.png")
        private val buttonResources = mutableMapOf(
            Orientation.TOP_LEFT to cobblemonResource("textures/gui/interact/button_left_top.png"),
            Orientation.TOP_RIGHT to cobblemonResource("textures/gui/interact/button_right_top.png"),
            Orientation.BOTTOM_LEFT to cobblemonResource("textures/gui/interact/button_left_bottom.png"),
            Orientation.BOTTOM_RIGHT to cobblemonResource("textures/gui/interact/button_right_bottom.png"),
        )
    }

    override fun init() {
        addButton(Orientation.TOP_LEFT, options[Orientation.TOP_LEFT])
        addButton(Orientation.TOP_RIGHT, options[Orientation.TOP_RIGHT])
        addButton(Orientation.BOTTOM_LEFT, options[Orientation.BOTTOM_LEFT])
        addButton(Orientation.BOTTOM_RIGHT, options[Orientation.BOTTOM_RIGHT])
    }

    private fun addButton(orientation: Orientation, option: InteractWheelOption?) {
        val (x, y) = getButtonPosition(orientation)
        addDrawableChild(InteractWheelButton(
            iconResource = option?.iconResource,
            buttonResource = buttonResources[orientation]!!,
            x = x,
            y = y,
            isEnabled = option != null,
            colour = option?.colour ?: { null },
            onPress = { option?.onPress?.invoke() }
        ))
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val (x, y) = getDimensions()
        blitk(
            matrixStack = context.matrices,
            texture = backgroundResource,
            x = x,
            y = y,
            width = SIZE,
            height = SIZE
        )
        super.render(context, mouseX, mouseY, delta)
    }

    private fun getDimensions(): Pair<Int, Int> {
        return Pair(
            (width - SIZE) / 2,
            (height - SIZE) / 2
        )
    }

    private fun getButtonPosition(orientation: Orientation): Pair<Int, Int> {
        val (x, y) = getDimensions()
        return when (orientation) {
            Orientation.TOP_LEFT -> Pair(x, y)
            Orientation.TOP_RIGHT -> Pair(x + OPTION_SIZE, y)
            Orientation.BOTTOM_LEFT -> Pair(x, y + OPTION_SIZE)
            Orientation.BOTTOM_RIGHT -> Pair(x + OPTION_SIZE, y + OPTION_SIZE)
        }
    }

    override fun shouldPause() = false

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isMouseInCenter(mouseX, mouseY)) return false
        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun isMouseInCenter(mouseX: Double, mouseY: Double): Boolean {
        val x = (((width - SIZE) / 2) + 44).toFloat()
        val xMax = x + 50
        val y = (((height - SIZE) / 2) + 44).toFloat()
        val yMax = y + 50
        return mouseX.toFloat() in x..xMax && mouseY.toFloat() in y..yMax
    }

}