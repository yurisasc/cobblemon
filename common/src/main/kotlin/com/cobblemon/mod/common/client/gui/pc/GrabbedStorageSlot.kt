/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pc

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.Widget
import net.minecraft.client.util.math.MatrixStack

class GrabbedStorageSlot(
    x: Int, y: Int,
    parent: StorageWidget,
    private val pokemon: Pokemon
) : StorageSlot(x, y, parent, {}) {
    private var lastFocused: StorageSlot? = null

    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val x: Int
        val y: Int

        parent.focused
            ?.let { it as? StorageSlot }
            ?.let { lastFocused = it }

        val lastFocused = lastFocused
        if (lastFocused != null && MinecraftClient.getInstance().navigationType.isKeyboard) {
            x = lastFocused.x + lastFocused.width / 2
            y = lastFocused.y + lastFocused.height / 2
        } else {
            x = mouseX
            y = mouseY
            // if the mouse got moved (or the last focus was null anyway) reset the last focused slot
            // so it doesn't get stuck in some random slot
            this.lastFocused = null
        }

        // make the held pokemon a little bigger, so it stands out
        val scale = 1.3f
        val (sw, sh) = (width * scale) to (height * scale)

        context.matrices.push()
        context.matrices.translate(0f, 0f, 50f)
        context.matrices.scale(scale, scale, 1f)

        renderSlot(
            context = context,
            posX = ((x - (sw / 2)) / scale).toInt(),
            posY = ((y - (sh / 2)) / scale).toInt(),
            partialTicks = delta,
            scissor = false
        )

        context.matrices.pop()

    }

    override fun isStationary() = false
    override fun getPokemon() = pokemon
    override fun isSelected() = true
}