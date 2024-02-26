/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pokedex.widgets

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUI
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

class ScrollBarWidget(val pX: Int, val pY: Int
): SoundlessWidget(pX, pY, PokedexGUI.SCROLL_BAR_WIDTH, PokedexGUI.SCROLL_HEIGHT, Text.literal("ScrollBar")) {

    var visualHeight = PokedexGUI.SCROLL_HEIGHT

    companion object {
        private val scrollBarResource = cobblemonResource("summary_scroll_overlay.png")
    }

    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        blitk(
            matrixStack = context.matrices,
            texture = scrollBarResource,
            x = pX,
            y = pY,
            width = PokedexGUI.SCROLL_BAR_WIDTH,
            height = visualHeight
        )
    }
}