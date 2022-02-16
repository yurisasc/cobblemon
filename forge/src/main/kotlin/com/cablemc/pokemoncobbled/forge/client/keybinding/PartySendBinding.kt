package com.cablemc.pokemoncobbled.forge.client.keybinding

import com.cablemc.pokemoncobbled.forge.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.sendToServer
import com.cablemc.pokemoncobbled.forge.common.net.messages.server.SendOutPokemonPacket
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object PartySendBinding : KeyMapping(
    "key.pokemoncobbled.throwpartypokemon",
    InputConstants.Type.KEYSYM,
    InputConstants.KEY_R,
    KeybindCategories.COBBLED_CATEGORY
) {
    var wasDown = false
    @SubscribeEvent
    fun onKeyInput(event: InputEvent.KeyInputEvent) {
        if (isDown && !wasDown) {
            wasDown = true
            if (PokemonCobbledClient.storage.selectedSlot != -1 && isConflictContextAndModifierActive && Minecraft.getInstance().screen == null) {
                sendToServer(SendOutPokemonPacket(PokemonCobbledClient.storage.selectedSlot))
            }
        } else if (!this.isDown) {
            wasDown = false
        }
    }
}