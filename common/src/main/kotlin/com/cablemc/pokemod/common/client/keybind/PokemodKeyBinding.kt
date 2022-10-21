/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.keybind

import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

/**
 * An extensions for Minecraft's [KeyBinding]
 * When creating a new [PokemodKeyBinding] [onPress] will be called when the key is pressed.
 *
 * @author Qu
 * @since 2022-02-17
 */
abstract class PokemodKeyBinding(
    name: String,
    type: InputUtil.Type = InputUtil.Type.KEYSYM,
    key: Int,
    category: String
): KeyBinding(name, type, key, category) {

    abstract fun onPress()

    open fun onKeyInput() {
        if (this.wasPressed()) {
            onPress()
        }
    }
}