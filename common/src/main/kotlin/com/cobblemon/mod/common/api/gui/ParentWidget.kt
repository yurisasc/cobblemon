/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.gui

import net.minecraft.client.gui.*
import net.minecraft.client.gui.Selectable.SelectionType
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text

/**
 * This class adds children-awareness to a Widget similar like a Screen
 * (otherwise the Widgets do not react on click/hover)
 */
abstract class ParentWidget(
    var x: Int, var y: Int,
    val width: Int, val height: Int,
    component: Text
) : AbstractParentElement(), Drawable, Selectable {
    var hovered = false
        private set
    val isHoveredOrFocused: Boolean
        get() = hovered || isFocused

    private val children: MutableList<Element> = mutableListOf()

    final override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        hovered = mouseX in x..(x + width) && mouseY in y..(y + height)

        for (child in children) {
            if (child is Drawable)
                child.render(context, mouseX, mouseY, delta)
        }

        renderWidget(context, mouseX, mouseY, delta)
    }

    abstract fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float)

    /**
     * Adds Widget to the children list
     */
    protected fun addWidget(widget: Element) {
        children.add(widget)
    }

    /**
     * Removes Widget from the children list
     */
    protected fun removeWidget(widget: Element) {
        children.remove(widget)
    }

    override fun children(): MutableList<out Element> = children

    override fun getNavigationFocus(): ScreenRect = ScreenRect(x, y, width, height)

    override fun getType(): SelectionType = when {
        isFocused -> SelectionType.FOCUSED
        else -> SelectionType.NONE
    }

    override fun appendNarrations(builder: NarrationMessageBuilder) {

    }

    protected open fun playDownSound(soundManager: SoundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}