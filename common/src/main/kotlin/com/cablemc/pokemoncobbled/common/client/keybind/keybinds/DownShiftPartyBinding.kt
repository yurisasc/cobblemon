/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.keybind.keybinds

import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeyBinding
import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import net.minecraft.client.util.InputUtil

object DownShiftPartyBinding : CobbledKeyBinding(
    "key.pokemoncobbled.downshiftparty",
    InputUtil.Type.KEYSYM,
    InputUtil.GLFW_KEY_DOWN,
    KeybindCategories.COBBLED_CATEGORY
) {
    override fun onPress() {
        PokemonCobbledClient.storage.shiftSelected(true)
    }
}