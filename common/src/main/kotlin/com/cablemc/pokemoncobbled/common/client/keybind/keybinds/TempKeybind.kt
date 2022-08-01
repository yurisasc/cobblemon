package com.cablemc.pokemoncobbled.common.client.keybind.keybinds

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.client.gui.startselection.StarterSelectionScreen
import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeyBinding
import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil

object TempKeybind : CobbledKeyBinding(
    "key.pokemoncobbled.starterselection",
    InputUtil.Type.KEYSYM,
    InputUtil.GLFW_KEY_L,
    KeybindCategories.COBBLED_CATEGORY
) {
    override fun onPress() {
        MinecraftClient.getInstance().setScreen(StarterSelectionScreen(PokemonCobbled.config.starters.map { it.asRenderableStarterCategory() }))
    }
}