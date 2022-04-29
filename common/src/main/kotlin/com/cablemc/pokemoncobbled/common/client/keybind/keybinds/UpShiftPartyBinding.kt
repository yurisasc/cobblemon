package com.cablemc.pokemoncobbled.common.client.keybind.keybinds

import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeyBinding
import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import net.minecraft.client.util.InputUtil

object UpShiftPartyBinding : CobbledKeyBinding(
    "key.pokemoncobbled.upshiftparty",
    InputUtil.Type.KEYSYM,
    InputUtil.GLFW_KEY_UP,
    KeybindCategories.COBBLED_CATEGORY
) {
    override fun onPress() {
        PokemonCobbledClient.storage.shiftSelected(false)
    }
}