package com.cablemc.pokemoncobbled.common.client.keybind.keybinds

import com.cablemc.pokemoncobbled.common.client.gui.pokenav.PokeNav
import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeyBinding
import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil

object PokeNavigatorBinding : CobbledKeyBinding(
    "key.pokemoncobbled.pokenavigator",
    InputUtil.Type.KEYSYM,
    InputUtil.GLFW_KEY_N,
    KeybindCategories.COBBLED_CATEGORY
) {
    override fun onPress() {
        MinecraftClient.getInstance().setScreen(PokeNav())
    }
}