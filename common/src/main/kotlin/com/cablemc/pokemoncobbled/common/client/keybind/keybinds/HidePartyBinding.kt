package com.cablemc.pokemoncobbled.common.client.keybind.keybinds

import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeyMapping
import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import com.mojang.blaze3d.platform.InputConstants

object HidePartyBinding : CobbledKeyMapping(
    "key.pokemoncobbled.hideparty",
    InputConstants.Type.KEYSYM,
    InputConstants.KEY_P,
    KeybindCategories.COBBLED_CATEGORY
) {
    var shouldHide = false

    override fun onPress() {
        shouldHide = !shouldHide
    }
}