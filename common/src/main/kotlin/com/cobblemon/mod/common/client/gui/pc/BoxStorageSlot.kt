/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pc

import com.cobblemon.mod.common.api.storage.pc.PCPosition
import com.cobblemon.mod.common.client.storage.ClientPC
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.client.gui.DrawContext

class BoxStorageSlot(
    x: Int, y: Int,
    parent: StorageWidget,
    private val pc: ClientPC,
    val position: PCPosition,
    onPress: PressAction
) : StorageSlot(x, y, parent, onPress) {

    override fun getPokemon(): Pokemon? {
        return pc.get(position)
    }

    override fun shouldRender(): Boolean {
        val grabbedSlot = parent.grabbedSlot
        return if (grabbedSlot == null) {
            true
        } else {
            grabbedSlot.getPokemon() != getPokemon()
        }
    }
}