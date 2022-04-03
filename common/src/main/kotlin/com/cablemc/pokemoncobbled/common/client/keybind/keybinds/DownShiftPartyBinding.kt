package com.cablemc.pokemoncobbled.common.client.keybind.keybinds

import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeyMapping
import com.mojang.blaze3d.platform.InputConstants

object DownShiftPartyBinding : CobbledKeyMapping(
    "key.pokemoncobbled.downshiftparty",
    InputConstants.Type.KEYSYM,
    InputConstants.KEY_DOWN,
    KeybindCategories.COBBLED_CATEGORY
) {
    override fun onPress() {
        PokemonCobbledClient.storage.shiftSelected(true)
    }
}