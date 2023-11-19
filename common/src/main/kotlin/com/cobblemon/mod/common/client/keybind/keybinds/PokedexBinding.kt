package com.cobblemon.mod.common.client.keybind.keybinds

import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.gui.pokedex.PokedexScreen
import com.cobblemon.mod.common.client.gui.summary.Summary
import com.cobblemon.mod.common.client.keybind.CobblemonPartyLockedKeyBinding
import com.cobblemon.mod.common.client.keybind.KeybindCategories
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil

object PokedexBinding : CobblemonPartyLockedKeyBinding(
    "key.cobblemon.pokedex",
    InputUtil.Type.KEYSYM,
    InputUtil.GLFW_KEY_K,
    KeybindCategories.COBBLEMON_CATEGORY
) {
    override fun onPress() {
        MinecraftClient.getInstance().setScreen(PokedexScreen(CobblemonClient.storage.myPokedex))
    }
}