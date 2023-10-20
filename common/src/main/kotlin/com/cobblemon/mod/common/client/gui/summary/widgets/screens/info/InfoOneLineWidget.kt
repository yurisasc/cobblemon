/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.summary.widgets.screens.info

import com.cobblemon.mod.common.api.text.*
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.MutableText
import net.minecraft.text.Text

class InfoOneLineWidget(
    pX: Int,
    pY: Int,
    width: Int,
    height: Int = ROW_HEIGHT,
    private val label: MutableText,
    private val value: MutableText,
    private val tooltip: Text? = null
): SoundlessWidget(pX, pY, width, height, Text.literal("InfoOneLineWidget")) {
    companion object {
        private val FONT = CobblemonResources.DEFAULT_LARGE
        private const val ROW_HEIGHT = 15
        private const val WITHIN_ROW_VERTICAL_OFFSET = 6
        private const val LABEL_HORIZONTAL_OFFSET = 8
        private const val VALUE_HORIZONTAL_OFFSET = 53
    }

    override fun renderButton(context: DrawContext, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {

        if (isHovered) {
            if (tooltip != null) {
                context.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltip, pMouseX, pMouseY)
            }
        }

        // Label
        drawScaledText(
            context = context,
            font = FONT,
            text = label.bold(),
            x = x + LABEL_HORIZONTAL_OFFSET,
            y = y + WITHIN_ROW_VERTICAL_OFFSET,
            shadow = true
        )
        // Value
        drawScaledText(
            context = context,
            font = FONT,
            text = value.bold(),
            x = x + VALUE_HORIZONTAL_OFFSET,
            y = y + WITHIN_ROW_VERTICAL_OFFSET,
            shadow = true
        )
    }

}
