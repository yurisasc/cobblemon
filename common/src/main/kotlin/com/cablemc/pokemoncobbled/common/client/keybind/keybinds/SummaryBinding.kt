package com.cablemc.pokemoncobbled.common.client.keybind.keybinds

import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeyBinding
import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import net.minecraft.client.util.InputUtil

object SummaryBinding : CobbledKeyBinding(
    "key.pokemoncobbled.summary",
    InputUtil.Type.KEYSYM,
    InputUtil.GLFW_KEY_M,
    KeybindCategories.COBBLED_CATEGORY
) {
    override fun onPress() {

    }
}