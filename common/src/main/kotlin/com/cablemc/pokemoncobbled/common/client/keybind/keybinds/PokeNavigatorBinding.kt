package com.cablemc.pokemoncobbled.common.client.keybind.keybinds

import com.cablemc.pokemoncobbled.common.api.text.red
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.pokenav.PokeNav
import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeyBinding
import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import com.cablemc.pokemoncobbled.common.net.messages.server.starter.RequestStarterScreenPacket
import com.cablemc.pokemoncobbled.common.util.lang
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil

object PokeNavigatorBinding : CobbledKeyBinding(
    "key.pokemoncobbled.pokenavigator",
    InputUtil.Type.KEYSYM,
    InputUtil.GLFW_KEY_N,
    KeybindCategories.COBBLED_CATEGORY
) {
    override fun onPress() {
        val havePokemon = PokemonCobbledClient.storage.myParty.slots.any { it != null }
        val starterSelected = PokemonCobbledClient.clientPlayerData.starterSelected
        val startersLocked = PokemonCobbledClient.clientPlayerData.starterLocked
        if (!starterSelected && !havePokemon) {
            if (startersLocked) {
                MinecraftClient.getInstance().player?.sendMessage(lang("ui.starterscreen.cannotchoose").red(), false)
            } else {
                RequestStarterScreenPacket().sendToServer()
            }
        } else  {
            MinecraftClient.getInstance().setScreen(PokeNav())
        }
    }
}