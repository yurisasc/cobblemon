package com.cablemc.pokemoncobbled.forge.client.keybinding

import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import com.cablemc.pokemoncobbled.forge.client.PokemonCobbledClient
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object DownShiftPartyBinding : KeyMapping(
    "key.pokemoncobbled.downshiftparty",
    InputConstants.Type.KEYSYM,
    InputConstants.KEY_DOWN,
    KeybindCategories.COBBLED_CATEGORY
) {
    var wasDown = false
    @SubscribeEvent
    fun onKeyInput(event: InputEvent.KeyInputEvent) {
        if (this.isDown && !wasDown) {
            PokemonCobbledClient.storage.shiftSelected(true)
            wasDown = true
        } else if (!this.isDown) {
            wasDown = false
        }
    }
}