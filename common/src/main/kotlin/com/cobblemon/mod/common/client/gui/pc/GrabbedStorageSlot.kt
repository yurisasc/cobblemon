/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pc

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.client.gui.DrawContext

class GrabbedStorageSlot(
    x: Int, y: Int,
    parent: StorageWidget,
    private val pokemon: Pokemon
) : StorageSlot(x, y, parent, {}) {

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderSlot(context = context, posX = mouseX - (width / 2), posY = mouseY - (height / 2), partialTicks = delta)
    }

    override fun isStationary() = false
    override fun getPokemon() = pokemon
    override fun isSelected() = true
}