package com.cablemc.pokemoncobbled.common.client.keybind.keybinds

import com.cablemc.pokemoncobbled.common.CobbledNetwork.sendToServer
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.battle.BattleGUI
import com.cablemc.pokemoncobbled.common.client.keybind.CobbledBlockingKeyBinding
import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.net.messages.server.BattleChallengePacket
import com.cablemc.pokemoncobbled.common.net.messages.server.SendOutPokemonPacket
import com.cablemc.pokemoncobbled.common.util.traceFirstEntityCollision
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.LivingEntity

object PartySendBinding : CobbledBlockingKeyBinding(
    "key.pokemoncobbled.throwpartypokemon",
    InputUtil.Type.KEYSYM,
    InputUtil.GLFW_KEY_R,
    KeybindCategories.COBBLED_CATEGORY
) {
    override fun onPress() {
        val player = MinecraftClient.getInstance().player ?: return
        val battle = PokemonCobbledClient.battle
        if (battle != null) {
            battle.minimised = !battle.minimised
            if (!battle.minimised ) {
                MinecraftClient.getInstance().setScreen(BattleGUI())
            }
            return
        }

        if (PokemonCobbledClient.storage.selectedSlot != -1 && MinecraftClient.getInstance().currentScreen == null) {
            val pokemon = PokemonCobbledClient.storage.myParty.get(PokemonCobbledClient.storage.selectedSlot)
            if (pokemon != null && pokemon.currentHealth > 0 ) {
                val targetedPokemon = player.traceFirstEntityCollision(entityClass = LivingEntity::class.java, ignoreEntity = player)
                if (targetedPokemon != null && (targetedPokemon !is PokemonEntity || targetedPokemon.canBattle(player))) {
                    sendToServer(BattleChallengePacket(targetedPokemon.id, pokemon.uuid))
                } else {
                    sendToServer(SendOutPokemonPacket(PokemonCobbledClient.storage.selectedSlot))
                }
            }
        }
    }
}