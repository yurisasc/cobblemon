/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.types.ElementalType
import net.minecraft.client.gui.DrawContext

class TypeIcon(
    val x: Number,
    val y: Number,
    val type: ElementalType,
    val secondaryType: ElementalType? = null,
    val centeredX: Boolean = false,
    val small: Boolean = false,
    val secondaryOffset: Float = 15F,
    val doubleCenteredOffset: Float = 7.5F,
    val opacity: Float = 1F
) {
    companion object {
        private const val TYPE_ICON_DIAMETER = 36
        private const val SCALE = 0.5F
    }

    fun render(context: DrawContext) {
        val diameter = if (small) (TYPE_ICON_DIAMETER / 2) else TYPE_ICON_DIAMETER
        val offsetX = if (centeredX) (((diameter / 2) * SCALE) + (if (secondaryType != null) (doubleCenteredOffset) else 0F)) else 0F

        if (secondaryType != null) {
            blitk(
                matrixStack = context.matrices,
                texture = if (small) secondaryType.clientData.secondaryTexture else secondaryType.clientData.primaryTexture,
                x = (x.toFloat() + secondaryOffset - offsetX) / SCALE,
                y = y.toFloat() / SCALE,
                height = diameter,
                width = diameter,
                uOffset = diameter * secondaryType.clientData.uOffset + 0.1,
                vOffset = diameter * secondaryType.clientData.vOffset + 0.1,
                textureWidth = diameter * 18,
                alpha = opacity,
                scale = SCALE
            )
        }

        blitk(
            matrixStack = context.matrices,
            texture = if (small) type.clientData.secondaryTexture else type.clientData.primaryTexture,
            x = (x.toFloat() - offsetX) / SCALE,
            y = y.toFloat() / SCALE,
            height = diameter,
            width = diameter,
            uOffset = diameter * type.clientData.uOffset + 0.1,
            vOffset = diameter * type.clientData.vOffset + 0.1,
            textureWidth = diameter * 18,
            alpha = opacity,
            scale = SCALE
        )
    }
}