/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.summary.widgets.common

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.text.MutableText

abstract class SummaryScrollList<T : AlwaysSelectedEntryListWidget.Entry<T>>(
    x: Int,
    y: Int,
    val label: MutableText,
    slotHeight: Int
) : AlwaysSelectedEntryListWidget<T>(
    MinecraftClient.getInstance(),
    WIDTH, // width
    HEIGHT, // height
    0, // top
    slotHeight
) {
    companion object {
        const val WIDTH = 108
        const val HEIGHT = 114
        const val SLOT_WIDTH = 91

        private val backgroundResource = cobblemonResource("textures/gui/summary/summary_scroll_background.png")
        private val scrollOverlayResource = cobblemonResource("textures/gui/summary/summary_scroll_overlay.png")
    }

    private var scrolling = false

    override fun getRowWidth(): Int {
        return SLOT_WIDTH
    }

    init {
        this.x = x
        this.y = y
        correctSize()
        setRenderBackground(false)
    }

    override fun getScrollbarPositionX(): Int {
        return x + width - 3
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val matrices = context.matrices
        correctSize()
        blitk(
            matrixStack = matrices,
            texture = backgroundResource,
            x = x,
            y = y,
            height = HEIGHT,
            width = WIDTH
        )

        context.enableScissor(
            x,
            y + 1,
            x + width,
            y + 1 + height
        )
        super.renderWidget(context, mouseX, mouseY, partialTicks)
        context.disableScissor()

        // Scroll Overlay
        val scrollOverlayOffset = 4
        blitk(
            matrixStack = matrices,
            texture = scrollOverlayResource,
            x = x,
            y = y - (scrollOverlayOffset / 2),
            height = HEIGHT + scrollOverlayOffset,
            width = WIDTH
        )

        // Label
        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = label.bold(),
            x = x + 32.5,
            y = y - 13.5,
            centered = true,
            shadow = true
        )
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        updateScrollingState(mouseX, mouseY)
        if (scrolling) {
            focused = getEntryAtPosition(mouseX, mouseY)
            isDragging = true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (scrolling) {
            if (mouseY < y) {
                setScrollAmount(0.0)
            } else if (mouseY > bottom) {
                setScrollAmount(maxScroll.toDouble())
            } else {
                setScrollAmount(scrollAmount + deltaY)
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    private fun updateScrollingState(mouseX: Double, mouseY: Double) {
        scrolling = mouseX >= this.scrollbarPositionX.toDouble()
                && mouseX < (this.scrollbarPositionX + 3).toDouble()
                && mouseY >= y
                && mouseY < bottom
    }

    private fun correctSize() {
        setDimensionsAndPosition(WIDTH, HEIGHT, y + 1, (y + 1) + (HEIGHT - 2))
        setX(x)
    }

    private fun scaleIt(i: Int): Int {
        return (client.window.scaleFactor * i).toInt()
    }
}