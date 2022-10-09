/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.keybind.keybinds

import com.cablemc.pokemod.common.PokemodNetwork.sendToServer
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.gui.battle.BattleGUI
import com.cablemc.pokemod.common.client.keybind.KeybindCategories
import com.cablemc.pokemod.common.client.keybind.PokemodBlockingKeyBinding
import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemod.common.net.messages.server.BattleChallengePacket
import com.cablemc.pokemod.common.net.messages.server.SendOutPokemonPacket
import com.cablemc.pokemod.common.util.traceFirstEntityCollision
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.LivingEntity

object PartySendBinding : PokemodBlockingKeyBinding(
    "key.pokemod.throwpartypokemon",
    InputUtil.Type.KEYSYM,
    InputUtil.GLFW_KEY_R,
    KeybindCategories.POKEMOD_CATEGORY
) {
    override fun onPress() {
        val player = MinecraftClient.getInstance().player ?: return
        val battle = PokemodClient.battle
        if (battle != null) {
            battle.minimised = !battle.minimised
            if (!battle.minimised ) {
                MinecraftClient.getInstance().setScreen(BattleGUI())
            }
            return
        }

        if (PokemodClient.storage.selectedSlot != -1 && MinecraftClient.getInstance().currentScreen == null) {
            val pokemon = PokemodClient.storage.myParty.get(PokemodClient.storage.selectedSlot)
            if (pokemon != null && pokemon.currentHealth > 0 ) {
                val targetedPokemon = player.traceFirstEntityCollision(entityClass = LivingEntity::class.java, ignoreEntity = player)
                if (targetedPokemon != null && (targetedPokemon !is PokemonEntity || targetedPokemon.canBattle(player))) {
                    sendToServer(BattleChallengePacket(targetedPokemon.id, pokemon.uuid))
                } else {
                    sendToServer(SendOutPokemonPacket(PokemodClient.storage.selectedSlot))
                }
            }
        }
    }
}