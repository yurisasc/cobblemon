/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.gui.pokenav

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.util.math.MatrixStack

class PokeNavFillerButton(
    posX: Int, posY: Int,
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    pTextureWidth: Int, pTextureHeight: Int
): PokeNavImageButton(posX, posY, pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, FILLER, pTextureWidth, pTextureHeight, {}, "".text()) {

    override fun renderButton(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        this.applyBlitk(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
        pMatrixStack.push()
    }

    override fun applyBlitk(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        blitk(
            matrixStack = pMatrixStack,
            texture = FILLER,
            x = x, y = y + 0.25,
            width = width, height = height,
            red = RED,
            green = GREEN,
            blue = BLUE,
            alpha = ALPHA
        )
    }

    companion object {

        const val RED = .28235
        const val GREEN = .29412
        const val BLUE = .30980
        const val ALPHA = .9

        private val FILLER = cobbledResource("ui/pokenav/pokenav_filler.png")

    }

}