/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pasture

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.pc.PCGUI
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class PastureWidget(
    val pasturePCGUIConfiguration: PasturePCGUIConfiguration,
    x: Int,
    y: Int
): SoundlessWidget(
    x, y, PCGUI.RIGHT_PANEL_WIDTH, PCGUI.RIGHT_PANEL_HEIGHT, Text.literal("PastureWidget")) {

    companion object {
        private val baseResource = cobblemonResource("textures/gui/pasture/pasture_panel.png")
    }

    private val recallButton = RecallButton(
        x = x + 6,
        y = y + 153
    ) {
        // TODO: Unpasture all pokemon
    }

    val pastureScrollList = PasturePokemonScrollList(
        x = x + 6,
        y = y + 31,
        parent = this
    )

    override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        blitk(
            matrixStack = matrices,
            texture = baseResource,
            x = x,
            y = y,
            width = PCGUI.RIGHT_PANEL_WIDTH,
            height = PCGUI.RIGHT_PANEL_HEIGHT
        )

        drawScaledText(
            matrixStack = matrices,
            font = CobblemonResources.DEFAULT_LARGE,
            text = lang("ui.pasture").bold(),
            x = x + 31.5,
            y = y + 3.5,
            centered = true
        )

        pastureScrollList.render(matrices, mouseX, mouseY, delta)

        recallButton.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (recallButton.isHovered(mouseX, mouseY)) recallButton.mouseClicked(mouseX, mouseY, button)
        if (pastureScrollList.isHovered(mouseX, mouseY)) pastureScrollList.mouseClicked(mouseX, mouseY, button)
        return super.mouseClicked(mouseX, mouseY, button)
    }
}