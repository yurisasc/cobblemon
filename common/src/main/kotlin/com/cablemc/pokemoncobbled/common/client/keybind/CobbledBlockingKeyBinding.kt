/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.keybind

import net.minecraft.client.util.InputUtil

/**
 * An extension for the [CobbledKeyBinding] to prevent the [onPress] from rapidly triggering when holding down the associated key
 *
 * @author Qu
 * @since 2022-02-23
 */
abstract class CobbledBlockingKeyBinding(
    name: String,
    type: InputUtil.Type = InputUtil.Type.KEYSYM,
    key: Int,
    category: String
) : CobbledKeyBinding(name, type, key, category) {
    private var wasDown = false

    override fun onKeyInput() {
        if (isPressed && !wasDown) {
            wasDown = true
            onPress()
        } else if (!isPressed)
            wasDown = false
    }
}