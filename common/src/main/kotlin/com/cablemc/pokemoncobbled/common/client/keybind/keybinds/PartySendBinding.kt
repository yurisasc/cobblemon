package com.cablemc.pokemoncobbled.common.client.keybind.keybinds

import com.cablemc.pokemoncobbled.common.CobbledNetwork.sendToServer
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.keybind.CobbledBlockingKeyBinding
import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.net.messages.server.ChallengePacket
import com.cablemc.pokemoncobbled.common.net.messages.server.SendOutPokemonPacket
import com.cablemc.pokemoncobbled.common.util.traceFirstEntityCollision
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil

object PartySendBinding : CobbledBlockingKeyBinding(
    "key.pokemoncobbled.throwpartypokemon",
    InputUtil.Type.KEYSYM,
    InputUtil.GLFW_KEY_R,
    KeybindCategories.COBBLED_CATEGORY
) {
    override fun onPress() {
        val player = MinecraftClient.getInstance().player
        
        val battle = PokemonCobbledClient.battle
        if (battle != null) {
            battle.minimised = !battle.minimised
            return
        }

        if (PokemonCobbledClient.storage.selectedSlot != -1 && MinecraftClient.getInstance().currentScreen == null && player != null) {
            val pokemon = PokemonCobbledClient.storage.myParty.get(PokemonCobbledClient.storage.selectedSlot)
            if (pokemon != null) {
                val targetedPokemon = player.traceFirstEntityCollision(entityClass = PokemonEntity::class.java)
                if (targetedPokemon != null && targetedPokemon.canBattle(player)) {
                    sendToServer(ChallengePacket(targetedPokemon.id, pokemon.uuid))
                } else {
                    sendToServer(SendOutPokemonPacket(PokemonCobbledClient.storage.selectedSlot))
                }
            }
        }
    }
}