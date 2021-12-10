package com.cablemc.pokemoncobbled.client.keybinding

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object DownShiftPartyBinding : KeyMapping(
    "key.pokemoncobbled.downshiftparty",
    InputConstants.Type.KEYSYM,
    InputConstants.KEY_DOWN,
    CATEGORY_GAMEPLAY
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