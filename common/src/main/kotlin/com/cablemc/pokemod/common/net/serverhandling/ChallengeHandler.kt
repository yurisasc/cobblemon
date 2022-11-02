/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.serverhandling

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.PokemodNetwork.sendPacket
import com.cablemc.pokemod.common.api.scheduling.after
import com.cablemc.pokemod.common.api.text.aqua
import com.cablemc.pokemod.common.api.text.red
import com.cablemc.pokemod.common.api.text.yellow
import com.cablemc.pokemod.common.battles.BattleBuilder
import com.cablemc.pokemod.common.battles.BattleRegistry
import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemod.common.net.PacketHandler
import com.cablemc.pokemod.common.net.messages.client.battle.ChallengeNotificationPacket
import com.cablemc.pokemod.common.net.messages.server.BattleChallengePacket
import com.cablemc.pokemod.common.util.lang
import com.cablemc.pokemod.common.util.party
import com.cablemc.pokemod.common.util.runOnServer
import net.minecraft.server.network.ServerPlayerEntity

object ChallengeHandler : PacketHandler<BattleChallengePacket> {
    override fun invoke(packet: BattleChallengePacket, ctx: PokemodNetwork.NetworkContext) {
        runOnServer {
            val player = ctx.player ?: return@runOnServer
            val targetedEntity = player.world.getEntityById(packet.targetedEntityId) ?: return@runOnServer
            val leadingPokemon = player.party()[packet.selectedPokemonId]?.uuid ?: return@runOnServer

            when (targetedEntity) {
                is PokemonEntity -> {
                    /*
                    if (targetedEntity.isOwner(player))
                        return@runOnServer
                     */
                    BattleBuilder.pve(player, targetedEntity, leadingPokemon).ifErrored { it.sendTo(player) { it.red() } }
                }
                is ServerPlayerEntity -> {
                    // Check in on battle requests, if the other player has challenged me, this starts the battle
                    val existingChallenge = BattleRegistry.pvpChallenges[targetedEntity.uuid]
                    if (existingChallenge != null && !existingChallenge.isExpired()) {
                        BattleBuilder.pvp1v1(player, targetedEntity)
                        BattleRegistry.pvpChallenges.remove(targetedEntity.uuid)
                    } else {
                        val challenge = BattleRegistry.BattleChallenge(targetedEntity.uuid)
                        BattleRegistry.pvpChallenges[player.uuid] = challenge
                        after(seconds = challenge.expiryTimeSeconds.toFloat()) {
                            BattleRegistry.pvpChallenges.remove(player.uuid, challenge)
                        }
                        targetedEntity.sendPacket(ChallengeNotificationPacket(player.name.copy().aqua()))
                        player.sendMessage(lang("challenge.sender", targetedEntity.name).yellow())
                    }
                }
                else -> {
                    // Unrecognized challenge target. NPCs will probably go here.
                }
            }
        }
    }
}