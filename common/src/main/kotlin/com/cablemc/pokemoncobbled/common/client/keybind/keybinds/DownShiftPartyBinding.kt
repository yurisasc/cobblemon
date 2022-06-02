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