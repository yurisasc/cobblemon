/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.startselection.widgets

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.startselection.StarterSelectionScreen
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.api.starter.RenderableStarterCategory
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class CategoryList(
    private val paneWidth: Int,
    private val paneHeight: Int,
    topOffset: Int,
    bottomOffset: Int,
    private val entryWidth: Int,
    entryHeight: Int,
    private val categories: Map<Identifier, RenderableStarterCategory>,
    val x: Int, val y: Int,
    private val minecraft: MinecraftClient = MinecraftClient.getInstance(),
    private val starterSelectionScreen: StarterSelectionScreen
) : AlwaysSelectedEntryListWidget<CategoryList.Category>(
    minecraft,
    paneWidth,
    paneHeight,
    topOffset,
    bottomOffset,
    entryHeight
) {

    companion object {
        private const val CATEGORY_BUTTON_WIDTH = 51.5f
        private const val CATEGORY_BUTTON_HEIGHT = 16f
        private val categoryResource = cobblemonResource("textures/gui/starterselection/starterselection_slot.png")
    }

    init {
        this.correctSize()
        this.setRenderHorizontalShadows(false)
        this.setRenderBackground(false)
        this.setRenderSelection(false)
        this.categories.forEach { (id, category) ->
            this.addEntry(Category(id, category))
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        context.enableScissor(
            x,
            y,
            x + width,
            y + height
        )
        super.render(context, mouseX, mouseY, delta)
        context.disableScissor()
    }

    private fun correctSize() {
        this.updateSize(this.paneWidth, this.paneHeight, this.y, this.y + this.paneHeight)
        this.setLeftPos(this.x)
    }
    override fun getRowWidth() = this.entryWidth
    override fun getScrollbarPositionX(): Int {
        return this.left + this.width - 5
    }


    inner class Category(private val id: Identifier, private val category: RenderableStarterCategory) : Entry<Category>() {

        override fun render(
            context: DrawContext,
            index: Int,
            y: Int,
            x: Int,
            entryWidth: Int,
            entryHeight: Int,
            mouseX: Int,
            mouseY: Int,
            hovered: Boolean,
            tickDelta: Float
        ) {
            val matrices = context.matrices
            val isHovered = mouseX >= x && mouseY >= y && mouseX < x + entryWidth && mouseY < y + (entryHeight - 1)
            if (isHovered) {
                blitk(
                    matrixStack = matrices,
                    x = x + 2.5f, y = y,
                    texture = categoryResource,
                    width = CATEGORY_BUTTON_WIDTH, height = CATEGORY_BUTTON_HEIGHT,
                    red = 0.75f, green = 0.75f, blue = 0.75f
                )
            } else
                blitk(
                    matrixStack = matrices,
                    x = x + 2.5f, y = y,
                    texture = categoryResource,
                    width = CATEGORY_BUTTON_WIDTH, height = CATEGORY_BUTTON_HEIGHT
                )
            drawScaledText(
                context = context,
                text = category.displayName.copy(),
                x = x + 28,
                y = y + 4.5F,
                scale = 1F,
                maxCharacterWidth = 50,
                shadow = true,
                centered = true
            )
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            starterSelectionScreen.changeCategory(this.id)
            minecraft.soundManager.play(PositionedSoundInstance.master(CobblemonSounds.GUI_CLICK, 1.0F))
            return true
        }

        override fun getNarration(): Text {
            return Text.of("Yes")
        }
    }
}