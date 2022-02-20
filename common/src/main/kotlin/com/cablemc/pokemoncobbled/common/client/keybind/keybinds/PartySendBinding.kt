package com.cablemc.pokemoncobbled.common.client.keybind.keybinds

import com.cablemc.pokemoncobbled.common.CobbledNetwork.sendToServer
import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeyMapping
import com.cablemc.pokemoncobbled.common.net.messages.server.SendOutPokemonPacket
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft

object PartySendBinding : CobbledKeyMapping(
    "key.pokemoncobbled.throwpartypokemon",
    InputConstants.Type.KEYSYM,
    InputConstants.KEY_R,
    KeybindCategories.COBBLED_CATEGORY
) {
    override fun onPress() {
        if (PokemonCobbledClient.storage.selectedSlot != -1 && Minecraft.getInstance().screen == null) {
            sendToServer(SendOutPokemonPacket(PokemonCobbledClient.storage.selectedSlot))
        }
    }
}