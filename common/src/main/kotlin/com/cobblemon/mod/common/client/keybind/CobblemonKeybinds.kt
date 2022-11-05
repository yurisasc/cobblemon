/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.keybind

import com.cobblemon.mod.common.client.keybind.keybinds.DownShiftPartyBinding
import com.cobblemon.mod.common.client.keybind.keybinds.HidePartyBinding
import com.cobblemon.mod.common.client.keybind.keybinds.PartySendBinding
import com.cobblemon.mod.common.client.keybind.keybinds.PokeNavigatorBinding
import com.cobblemon.mod.common.client.keybind.keybinds.UpShiftPartyBinding

/**
 * Main registry for Keybinds
 *
 * @author Qu
 * @since 2022-02-17
 */
object CobblemonKeybinds {

    val keybinds = listOf(
        HidePartyBinding,
        PokeNavigatorBinding,
        DownShiftPartyBinding,
        PartySendBinding,
        UpShiftPartyBinding
    )

    fun onAnyKey(key: Int, scanCode: Int, action: Int, modifiers: Int) = keybinds.forEach(CobblemonKeyBinding::onKeyInput)
}