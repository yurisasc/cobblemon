package com.cablemc.pokemoncobbled.common.client.keybind

import com.cablemc.pokemoncobbled.common.client.keybind.keybinds.*
import dev.architectury.registry.client.keymappings.KeyMappingRegistry

/**
 * Main registry for Keybinds
 *
 * @author Qu
 * @since 2022-02-17
 */
object CobbledKeybinds {

    private val keybinds = mutableListOf<CobbledKeyBinding>()
    fun register() {
        registerKeybind(HidePartyBinding)
        registerKeybind(PokeNavigatorBinding)
        registerKeybind(DownShiftPartyBinding)
        registerKeybind(PartySendBinding)
        registerKeybind(UpShiftPartyBinding)
        registerKeybind(TempKeybind)

        keybinds.forEach { KeyMappingRegistry.register(it) }
    }

    fun onAnyKey(key: Int, scanCode: Int, action: Int, modifiers: Int) {
        keybinds.toMutableList().forEach {
            it.onKeyInput()
        }
    }

    fun getAllKeybinds() = keybinds

    private fun registerKeybind(keybind: CobbledKeyBinding): CobbledKeyBinding {
        return keybind.also {
            keybinds.add(it)
        }
    }
}