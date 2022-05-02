package com.cablemc.pokemoncobbled.common.client.keybind.keybinds

import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeyBinding
import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import net.minecraft.client.util.InputUtil

object HidePartyBinding : CobbledKeyBinding(
    "key.pokemoncobbled.hideparty",
    InputUtil.Type.KEYSYM,
    InputUtil.GLFW_KEY_P,
    KeybindCategories.COBBLED_CATEGORY
) {
    var shouldHide = false

    override fun onPress() {
        shouldHide = !shouldHide
    }
}