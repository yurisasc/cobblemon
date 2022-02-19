package com.cablemc.pokemoncobbled.common.client.keybind

import com.cablemc.pokemoncobbled.common.client.keybind.keybinds.HidePartyBinding
import com.cablemc.pokemoncobbled.common.client.keybind.keybinds.PokeNavigatorBinding
import dev.architectury.registry.client.keymappings.KeyMappingRegistry

/**
 * Main registry for Keybinds
 *
 * @author Qu
 * @since 2022-02-17
 */
object CobbledKeybinds {
    private val keybinds = mutableListOf<CobbledKeyMapping>()
    fun register() {
        registerKeybind(HidePartyBinding)
        registerKeybind(PokeNavigatorBinding)

        keybinds.forEach { KeyMappingRegistry.register(it) }
    }

    fun onAnyKey(key: Int, scanCode: Int, action: Int, modifiers: Int) {
        keybinds.toMutableList().forEach {
            it.onKeyInput()
        }
    }

    fun getAllKeybinds() = keybinds

    private fun registerKeybind(keybind: CobbledKeyMapping): CobbledKeyMapping {
        return keybind.also {
            keybinds.add(it)
        }
    }
}