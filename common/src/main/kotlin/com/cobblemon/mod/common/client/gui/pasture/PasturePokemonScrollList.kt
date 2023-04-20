/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pasture

import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.render.drawScaledText
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText

class PasturePokemonScrollList(
    val x: Int,
    val y: Int,
    val label: MutableText,
    slotHeight: Int
) : AlwaysSelectedEntryListWidget<PasturePokemonEntry>(
    MinecraftClient.getInstance(),
    WIDTH, // width
    HEIGHT, // height
    0, // top
    HEIGHT, // bottom
    slotHeight
) {
    companion object {
        const val WIDTH = 108
        const val HEIGHT = 114
        const val SLOT_WIDTH = 91

//        private val backgroundResource = cobblemonResource("textures/gui/summary/summary_scroll_background.png")
//        private val scrollOverlayResource = cobblemonResource("textures/gui/summary/summary_scroll_overlay.png")
    }

    private var scrolling = false

    override fun getRowWidth(): Int {
        return SLOT_WIDTH
    }

    init {
        correctSize()
        setRenderHorizontalShadows(false)
        setRenderBackground(false)
        setRenderSelection(false)
    }

    override fun getScrollbarPositionX(): Int {
        return left + width - 3
    }

    public override fun addEntry(entry: PasturePokemonEntry): Int {
        return super.addEntry(entry)
    }
    public override fun removeEntry(entry: PasturePokemonEntry): Boolean  {
        return super.removeEntry(entry)
    }

    override fun render(poseStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        correctSize()

//        blitk(
//            matrixStack = poseStack,
//            texture = backgroundResource,
//            x = left,
//            y = top,
//            height = HEIGHT,
//            width = WIDTH
//        )

        DrawableHelper.enableScissor(
            left,
            top + 1,
            left + width,
            top + 1 + height
        )
        super.render(poseStack, mouseX, mouseY, partialTicks)
        DrawableHelper.disableScissor()

        // Scroll Overlay
        val scrollOverlayOffset = 4
//        blitk(
//            matrixStack = poseStack,
//            texture = scrollOverlayResource,
//            x = left,
//            y = top - (scrollOverlayOffset / 2),
//            height = HEIGHT + scrollOverlayOffset,
//            width = WIDTH
//        )

        // Label
        drawScaledText(
            matrixStack = poseStack,
            font = CobblemonResources.DEFAULT_LARGE,
            text = label.bold(),
            x = left + 32.5,
            y = top - 13.5,
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
            if (mouseY < top) {
                scrollAmount = 0.0
            } else if (mouseY > bottom) {
                scrollAmount = maxScroll.toDouble()
            } else {
                scrollAmount = scrollAmount + deltaY
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    private fun updateScrollingState(mouseX: Double, mouseY: Double) {
        scrolling = mouseX >= this.scrollbarPositionX.toDouble()
                && mouseX < (this.scrollbarPositionX + 3).toDouble()
                && mouseY >= top
                && mouseY < bottom
    }

    private fun correctSize() {
        updateSize(WIDTH, HEIGHT, y + 1, (y + 1) + (HEIGHT - 2))
        setLeftPos(x)
    }
}