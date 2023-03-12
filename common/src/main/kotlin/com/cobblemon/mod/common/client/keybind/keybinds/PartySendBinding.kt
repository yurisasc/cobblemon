/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.keybind.keybinds

import com.cobblemon.mod.common.CobblemonNetwork.sendPacketToServer
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.gui.battle.BattleGUI
import com.cobblemon.mod.common.client.keybind.CobblemonBlockingKeyBinding
import com.cobblemon.mod.common.client.keybind.KeybindCategories
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.net.messages.server.BattleChallengePacket
import com.cobblemon.mod.common.net.messages.server.SendOutPokemonPacket
import com.cobblemon.mod.common.util.traceFirstEntityCollision
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.LivingEntity

object PartySendBinding : CobblemonBlockingKeyBinding(
    "key.cobblemon.throwpartypokemon",
    InputUtil.Type.KEYSYM,
    InputUtil.GLFW_KEY_R,
    KeybindCategories.COBBLEMON_CATEGORY
) {
    override fun onPress() {
        val player = MinecraftClient.getInstance().player ?: return
        val battle = CobblemonClient.battle
        if (battle != null) {
            battle.minimised = !battle.minimised
            if (!battle.minimised ) {
                MinecraftClient.getInstance().setScreen(BattleGUI())
            }
            return
        }

        if (CobblemonClient.storage.selectedSlot != -1 && MinecraftClient.getInstance().currentScreen == null) {
            val pokemon = CobblemonClient.storage.myParty.get(CobblemonClient.storage.selectedSlot)
            if (pokemon != null && pokemon.currentHealth > 0 ) {
                val targetedPokemon = player.traceFirstEntityCollision(entityClass = LivingEntity::class.java, ignoreEntity = player)
                if (targetedPokemon != null && (targetedPokemon !is PokemonEntity || targetedPokemon.canBattle(player))) {

                    // Interaction GUI

                    sendPacketToServer(BattleChallengePacket(targetedPokemon.id, pokemon.uuid))
                } else {
                    sendPacketToServer(SendOutPokemonPacket(CobblemonClient.storage.selectedSlot))
                }
            }
        }
    }
}