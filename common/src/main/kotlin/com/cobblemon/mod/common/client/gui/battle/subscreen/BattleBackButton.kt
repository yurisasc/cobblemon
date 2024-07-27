/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.battle.subscreen

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget

class BattleBackButton(
    x: Int,
    y: Int,
    backAction: (ButtonWidget) -> Unit
) : ButtonWidget(x, y, WIDTH, HEIGHT, "Back".text(), backAction, DEFAULT_NARRATION_SUPPLIER) {
    companion object {
        const val WIDTH = 58
        const val HEIGHT = 34
        const val SCALE = 0.5F
    }

    override fun renderButton(drawContext: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        blitk(
            matrixStack = drawContext.matrices,
            texture = cobblemonResource("textures/gui/battle/battle_back.png"),
            x = x * 2,
            y = y * 2,
            height = HEIGHT,
            width = WIDTH,
            vOffset = if (isHovered || isFocused) HEIGHT else 0,
            textureHeight = HEIGHT * 2,
            scale = SCALE
        )
    }
}