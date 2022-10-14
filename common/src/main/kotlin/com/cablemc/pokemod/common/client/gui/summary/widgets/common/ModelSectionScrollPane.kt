/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.gui.summary.widgets.common

import com.cablemc.pokemod.common.api.gui.blitk
import com.cablemc.pokemod.common.client.gui.summary.widgets.ModelWidget
import com.cablemc.pokemod.common.client.gui.summary.widgets.pages.moves.change.MoveSwitchPane
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

abstract class ModelSectionScrollPane<T : AlwaysSelectedEntryListWidget.Entry<T>>(
    private val overlayTexture: Identifier,
    private val paneWidth: Int = PANE_WIDTH,
    private val paneHeight: Int = PANE_HEIGHT,
    topOffset: Int,
    bottomOffset: Int,
    private val entryWidth: Int,
    entryHeight: Int
) : AlwaysSelectedEntryListWidget<T>(
    MinecraftClient.getInstance(),
    paneWidth,
    paneHeight,
    topOffset,
    bottomOffset,
    entryHeight
) {

    private var entriesCreated = false

    init {
        this.correctSize()
        this.setRenderHorizontalShadows(false)
        this.setRenderBackground(false)
        this.setRenderSelection(false)
    }

    protected val scaledX: Int
        get() = this.client.window.scaledWidth / 2 + 13
    protected val scaledY: Int
        get() = this.client.window.scaledHeight / 2 - 75

    override fun getScrollbarPositionX(): Int {
        return this.left + this.width - 12
    }

    final override fun getRowWidth() = this.entryWidth

    override fun render(poseStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (!this.entriesCreated) {
            this.createEntries().forEach { entry -> this.addEntry(entry) }
            this.entriesCreated = true
        }
        correctSize()
        ModelWidget.render = false
        blitk(
            matrixStack = poseStack,
            texture = this.overlayTexture,
            x = this.left,
            y = this.top - 4,
            height = MoveSwitchPane.PANE_HEIGHT,
            width = MoveSwitchPane.PANE_WIDTH
        )
        RenderSystem.enableScissor(this.scale(this.left + 2), this.client.window.height / 2 - this.scale(96), this.scale(this.width - 4), this.scale(this.height))
        super.render(poseStack, mouseX, mouseY, partialTicks)
        RenderSystem.disableScissor()
    }

    protected fun correctSize() {
        this.updateSize(this.paneWidth, this.paneHeight - 6, this.scaledY, this.scaledY + this.paneHeight - 4)
        this.setLeftPos(this.scaledX)
    }

    protected abstract fun createEntries(): Collection<T>

    private fun scale(n: Int): Int = (this.client.window.scaleFactor * n).toInt()

    companion object {

        private const val PANE_WIDTH = 117
        private const val PANE_HEIGHT = 178

    }

}