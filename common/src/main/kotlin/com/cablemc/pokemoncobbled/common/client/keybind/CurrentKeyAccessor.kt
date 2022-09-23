/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.keybind

import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil


/**
 * Interface for us to retrieve the current [Key] if it has been changed from the default [Key].
 *
 * @author Qu
 * @since 2022-02-17
 */
interface CurrentKeyAccessor {
    fun currentKey(): InputUtil.Key
}

fun KeyBinding.currentKey(): InputUtil.Key {
    return (this as CurrentKeyAccessor).currentKey()
}