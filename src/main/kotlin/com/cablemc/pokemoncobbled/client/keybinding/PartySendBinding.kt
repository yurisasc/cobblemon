package com.cablemc.pokemoncobbled.client.keybinding

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.net.PokemonCobbledNetwork
import com.cablemc.pokemoncobbled.common.net.messages.server.SendOutPokemonPacket
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object PartySendBinding : KeyMapping(
    "key.pokemoncobbled.throwpartypokemon",
    InputConstants.Type.KEYSYM,
    InputConstants.KEY_R,
    CATEGORY_GAMEPLAY
) {
    var wasDown = false
    @SubscribeEvent
    fun onKeyInput(event: InputEvent.KeyInputEvent) {
        if (isDown && !wasDown) {
            wasDown = true
            if (PokemonCobbledClient.storage.selectedSlot != -1) {
                PokemonCobbledNetwork.sendToServer(SendOutPokemonPacket(PokemonCobbledClient.storage.selectedSlot))
            }
        } else if (!this.isDown) {
            wasDown = false
        }
    }
}