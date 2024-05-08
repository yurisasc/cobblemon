/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.battle.subscreen

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.Selectable.SelectionType
import net.minecraft.client.gui.navigation.GuiNavigation
import net.minecraft.client.gui.navigation.GuiNavigationPath
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.input.KeyCodes
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.math.MatrixStack

class BattleBackButton(val x: Float, val y: Float, private val backAction: () -> Unit) : Element, Drawable, Selectable {
    private var focused = false
    var hovered = false
        private set

    companion object {
        const val WIDTH = 58
        const val HEIGHT = 34
        const val SCALE = 0.5F
    }

    override fun render(drawContext: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        hovered = isHovered(mouseX.toDouble(), mouseY.toDouble())

        blitk(
            matrixStack = drawContext.matrices,
            texture = cobblemonResource("textures/gui/battle/battle_back.png"),
            x = x * 2,
            y = y * 2,
            height = HEIGHT,
            width = WIDTH,
            vOffset = if (hovered || isFocused) HEIGHT else 0,
            textureHeight = HEIGHT * 2,
            scale = SCALE
        )
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isHovered(mouseX, mouseY) && button == 0) {
            backAction()
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (KeyCodes.isToggle(keyCode)) {
            backAction()
            return true
        }

        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    fun isHovered(mouseX: Double, mouseY: Double) = mouseX.toFloat() in (x..(x + (WIDTH * SCALE))) && mouseY.toFloat() in (y..(y + (HEIGHT * SCALE)))

    override fun getNavigationPath(navigation: GuiNavigation?): GuiNavigationPath? {
        return GuiNavigationPath.of(this).takeIf { !isFocused }
    }

    override fun getNavigationFocus(): ScreenRect =
        ScreenRect(x.toInt(), y.toInt(), ((WIDTH * SCALE).toInt()), (HEIGHT * SCALE).toInt())

    override fun isFocused(): Boolean = focused
    override fun setFocused(focused: Boolean) {
        this.focused = focused
    }

    override fun getType(): SelectionType = when {
        isFocused -> SelectionType.FOCUSED
        hovered -> SelectionType.HOVERED
        else -> SelectionType.NONE
    }

    override fun appendNarrations(builder: NarrationMessageBuilder?) {

    }
}