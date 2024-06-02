/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.serverhandling

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.api.scheduling.afterOnServer
import com.cobblemon.mod.common.api.text.aqua
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.text.yellow
import com.cobblemon.mod.common.battles.BattleBuilder
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.net.messages.client.battle.BattleChallengeNotificationPacket
import com.cobblemon.mod.common.net.messages.server.BattleChallengePacket
import com.cobblemon.mod.common.util.getPlayer
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.party
import java.util.UUID
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

object ChallengeHandler : ServerNetworkPacketHandler<BattleChallengePacket> {
    override fun handle(packet: BattleChallengePacket, server: MinecraftServer, player: ServerPlayerEntity) {
        if(player.isSpectator) return

        val targetedEntity = player.world.getEntityById(packet.targetedEntityId)?.let {
            if (it is PokemonEntity) {
                val owner = it.owner
                if (owner != null) {
                    return@let owner
                }
            }
            return@let it
        } ?: return
        val leadingPokemon = player.party()[packet.selectedPokemonId]?.uuid ?: return


        when (targetedEntity) {
            is PokemonEntity -> {
                if (!targetedEntity.canBattle(player)) {
                    return
                }
                /*
                if (targetedEntity.isOwner(player))
                    return@runOnServer
                 */
                BattleBuilder.pve(player, targetedEntity, leadingPokemon).ifErrored { it.sendTo(player) { it.red() } }
            }
            is ServerPlayerEntity -> {
                // Bandaid for odd desync thing with data tracker
                if (player == targetedEntity) {
                    return
                }
                val existingChallenge = BattleRegistry.pvpChallenges[player.uuid]
                if (existingChallenge != null && !existingChallenge.isExpired() && existingChallenge.challengedPlayerUUID == targetedEntity.uuid) {
                    // Overwrite the challenge or do nothing.
                    // send a message about there being an existing challenge
                    player.sendMessage(lang("challenge.pending", targetedEntity.name).yellow())
                } else {
                    if (packet.battleType == "multi") {
                        // check for team
                        val existingPlayerTeam = BattleRegistry.playerToTeam[player.uuid]
                        val existingTargetTeam = BattleRegistry.playerToTeam[targetedEntity.uuid]
                        if(existingPlayerTeam != null && existingTargetTeam != null && existingTargetTeam.teamID != existingPlayerTeam.teamID) {
                            // Send a request to start a battle
                            val challenge = BattleRegistry.BattleChallenge(UUID.randomUUID(), existingTargetTeam.teamID, leadingPokemon, packet.battleType)
                            BattleRegistry.pvpChallenges[existingPlayerTeam.teamID] = challenge
                            afterOnServer(seconds = challenge.expiryTimeSeconds.toFloat()) {
                                BattleRegistry.removeChallenge(existingPlayerTeam.teamID, challengeId = challenge.challengeId)
                            }
                            // Notify everyone of the challenge

                            // Notify challenging team
                            existingPlayerTeam.teamPlayersUUID.forEach { UUID ->
                                val serverPlayerEntity = UUID.getPlayer()
                                serverPlayerEntity?.sendMessage(lang("challenge.multi.sender", targetedEntity.name, existingTargetTeam.teamPlayersUUID.size))
                            }
                            // Notify challenged tam
                            CobblemonNetwork.sendPacketToPlayers(
                                existingTargetTeam.teamPlayersUUID.map { it.getPlayer() }.mapNotNull { it },
                                BattleChallengeNotificationPacket(challenge.challengeId, player.uuid, player.name.copy().aqua(), "challenge.multibattle")
                            )
                        }
                    } else {
                        val challenge = BattleRegistry.BattleChallenge(UUID.randomUUID(), targetedEntity.uuid, leadingPokemon, packet.battleType)
                        BattleRegistry.pvpChallenges[player.uuid] = challenge
                        afterOnServer(seconds = challenge.expiryTimeSeconds.toFloat()) {
                            BattleRegistry.removeChallenge(player.uuid, challengeId = challenge.challengeId)
                        }

                        val battleFormatLang = when (packet.battleType) {
                            "doubles" -> "challenge.doublebattle"
                            "triples" -> "challenge.triplebattle"
                            "multi" -> "challenge.multibattle"
                            else -> "challenge.singlebattle"
                        }

                        targetedEntity.sendPacket(BattleChallengeNotificationPacket(challenge.challengeId, player.uuid, player.name.copy().aqua(), battleFormatLang))
                        player.sendMessage(lang("challenge.sender", targetedEntity.name, lang(battleFormatLang)).yellow())
                    }
                }
            }
            else -> {
                // Unrecognized challenge target. NPCs will probably go here.
            }
        }
    }
}