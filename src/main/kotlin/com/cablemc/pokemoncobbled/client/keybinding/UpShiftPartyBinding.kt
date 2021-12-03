package com.cablemc.pokemoncobbled.client.keybinding

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object UpShiftPartyBinding : KeyMapping(
    "key.pokemoncobbled.upshiftparty",
    InputConstants.Type.KEYSYM,
    InputConstants.KEY_UP,
    CATEGORY_GAMEPLAY
) {
    var wasDown = false
    @SubscribeEvent
    fun onKeyInput(event: InputEvent.KeyInputEvent) {
        if (this.isDown && !wasDown) {
            PokemonCobbledClient.storage.shiftSelected(false)
            wasDown = true
        } else if (!this.isDown) {
            wasDown = false
        }
    }
}