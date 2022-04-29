package com.cablemc.pokemoncobbled.common.client.keybind.keybinds

import com.cablemc.pokemoncobbled.common.CobbledNetwork.sendToServer
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.keybind.CobbledBlockingKeyMapping
import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.net.messages.server.ChallengePacket
import com.cablemc.pokemoncobbled.common.net.messages.server.SendOutPokemonPacket
import com.cablemc.pokemoncobbled.common.util.traceFirstEntityCollision
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft

object PartySendBinding : CobbledBlockingKeyMapping(
    "key.pokemoncobbled.throwpartypokemon",
    InputConstants.Type.KEYSYM,
    InputConstants.KEY_R,
    KeybindCategories.COBBLED_CATEGORY
) {
    override fun onPress() {
        val player = MinecraftClient.getInstance().player
        if (PokemonCobbledClient.storage.selectedSlot != -1 && MinecraftClient.getInstance().screen == null && player != null) {
            val pokemon = PokemonCobbledClient.storage.myParty.get(PokemonCobbledClient.storage.selectedSlot)
            if (pokemon != null) {
                val targetedPokemon = player.traceFirstEntityCollision(entityClass = PokemonEntity::class.java)
                if (targetedPokemon != null) {
                    if (targetedPokemon.canBattle(player)) {
                        sendToServer(ChallengePacket(targetedPokemon.id, pokemon.uuid))
                    }
                } else {
                    sendToServer(SendOutPokemonPacket(PokemonCobbledClient.storage.selectedSlot))
                }
            }
        }
    }
}