/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.battle.widgets

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.gui.battle.BattleGUI
import com.cobblemon.mod.common.client.render.drawScaledText
import net.minecraft.client.gui.*
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.PressableWidget
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

class BattleOptionTile(
    val battleGUI: BattleGUI,
    x: Int,
    y: Int,
    val resource: Identifier,
    val text: MutableText,
    val onClick: () -> Unit
) : PressableWidget(x, y, OPTION_WIDTH, OPTION_HEIGHT, text) {
    companion object {
        const val OPTION_WIDTH = 90
        const val OPTION_HEIGHT = 26
    }

    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val opacity = CobblemonClient.battleOverlay.opacityRatio
        if (opacity < 0.1) {
            return
        }
        blitk(
            matrixStack = context.matrices,
            texture = resource,
            x = x,
            y = y,
            alpha = opacity,
            width = OPTION_WIDTH,
            height = OPTION_HEIGHT,
            vOffset = if (isHovered || isFocused) OPTION_HEIGHT else 0,
            textureHeight = OPTION_HEIGHT * 2
        )

        val scale = 1F
        drawScaledText(
            context = context,
            text = text,
            x = x + 6,
            y = y + 8,
            opacity = opacity,
            scale = scale,
            shadow = true
        )
    }

    override fun onPress() {
        onClick()
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder) {

    }
}