/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.serverhandling

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.text.yellow
import com.cobblemon.mod.common.battles.BattleBuilder
import com.cobblemon.mod.common.battles.BattleFormat
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.net.messages.server.BattleChallengeResponsePacket
import com.cobblemon.mod.common.util.battleLang
import com.cobblemon.mod.common.util.getPlayer
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.party
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d

object ChallengeResponseHandler : ServerNetworkPacketHandler<BattleChallengeResponsePacket> {
    override fun handle(packet: BattleChallengeResponsePacket, server: MinecraftServer, player: ServerPlayerEntity) {
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
                // Check in on battle requests, if the other player has challenged me, this starts the battle
                val existingChallenge = BattleRegistry.pvpChallenges[targetedEntity.uuid] ?: BattleRegistry.pvpChallenges[BattleRegistry.playerToTeam[targetedEntity.uuid]?.teamID]
                var existingChallengePokemon = existingChallenge?.selectedPokemonId
                if (existingChallenge != null && !existingChallenge.isExpired() && (existingChallenge.challengedPlayerUUID == player.uuid || existingChallenge.challengedPlayerUUID == BattleRegistry.playerToTeam[player.uuid]?.teamID)) {

                    val battleFormat = when (existingChallenge.battleType) {
                        "doubles" -> BattleFormat.GEN_9_DOUBLES
                        "triples" -> BattleFormat.GEN_9_TRIPLES
                        "multi" -> BattleFormat.GEN_9_MULTI
                        else -> BattleFormat.GEN_9_SINGLES
                    }

                    if(packet.accept) {
                        if(existingChallenge.battleType == "multi") {
                            // Start a multibattle
                            val team1 = BattleRegistry.playerToTeam[targetedEntity.uuid]?.teamPlayersUUID
                            val team2 = BattleRegistry.playerToTeam[player.uuid]?.teamPlayersUUID
                            val targetTeamUUID = BattleRegistry.playerToTeam[targetedEntity.uuid]?.teamID
                            if(team1 == null || team2 == null || targetTeamUUID == null) {
                                // Error message
                                return
                            }

                            val players = (team1+team2).map { uuid -> uuid.getPlayer() }.mapNotNull { it }

                            if(players.count() != BattleRegistry.MAX_TEAM_MEMBER_COUNT * 2) {
                                // TODO: handle larger teams
                                BattleRegistry.removeChallenge(targetTeamUUID, existingChallenge.challengeId, true)
                                // Send error message that we don't have the correct number of players
                                players.forEach {
                                    it.sendMessage(battleLang( "error.incorrect_player_count", players.count(), BattleRegistry.MAX_TEAM_MEMBER_COUNT).red())
                                }
                                return
                            }

                            val leadPokemon = players.mapNotNull { serverPlayerEntity ->
                                serverPlayerEntity.party().first().uuid
                            }

                            if(leadPokemon.count() != players.count()) {
                                // TODO: Error Message, missing team leads
                                BattleRegistry.removeChallenge(targetTeamUUID, existingChallenge.challengeId, true)
                                return
                            }

                            // Check if all players are in the same minecraft dimension
                            val dimension = player.world.dimension
                            val playerInWrongDimension = players.firstOrNull { it.world.dimension != dimension }
                            if(playerInWrongDimension != null) {
                                // TODO: Error message, not all players in the same Minecraft dimension
                                players.forEach {
                                    it.sendMessage(battleLang("error.player_different_dimension"))
                                }
                                BattleRegistry.removeChallenge(targetTeamUUID, existingChallenge.challengeId, true)
                                return
                            }

                            // Check if all players all nearby
                            var averagePos = Vec3d(0.0, 0.0, 0.0)
                            players.forEach {
                                averagePos = averagePos.add(it.pos.multiply(1.0 / players.count(), 0.0, 1.0 / players.count()))
                            }
                            val farAwayPlayer = players.firstOrNull { it.pos.subtract(0.0, it.pos.y, 0.0) .squaredDistanceTo(averagePos) > BattleRegistry.MAX_BATTLE_RADIUS * BattleRegistry.MAX_BATTLE_RADIUS }
                            if(farAwayPlayer != null) {
                                // Error message, player too far away
                                players.forEach {
                                    val langKey = if(it.uuid == farAwayPlayer.uuid) "error.player_distance.personal" else "error.player_distance"
                                    it.sendMessage(battleLang(langKey, farAwayPlayer.name))
                                }
                                BattleRegistry.removeChallenge(targetTeamUUID, existingChallenge.challengeId, true)
                                return
                            }

                            BattleBuilder.pvp2v2(players, leadPokemon, battleFormat)
                            BattleRegistry.removeChallenge(targetTeamUUID, existingChallenge.challengeId, true)
                        } else {
                            if (targetedEntity.party()[existingChallengePokemon!!] == null) {
                                if (targetedEntity.party().none()) {
                                    player.sendMessage(battleLang("error.no_pokemon_opponent"))
                                    targetedEntity.sendMessage(battleLang("error.no_pokemon"))
                                    BattleRegistry.removeChallenge(targetedEntity.uuid)
                                    return
                                }
                                existingChallengePokemon = targetedEntity.party().first().uuid
                            }
                            BattleBuilder.pvp1v1(player, targetedEntity, leadingPokemon, existingChallengePokemon, battleFormat)
                            BattleRegistry.removeChallenge(targetedEntity.uuid, existingChallenge.challengeId)
                        }

                    } else {
                        // Play messages to both sides that the battle was declined
                        // TODO: Will need a more generic condition when more multibattle rulesets are available
                        if (battleFormat == BattleFormat.GEN_9_MULTI) {
                            val targetedTeam = BattleRegistry.playerToTeam[targetedEntity.uuid]
                            if (targetedTeam != null) {
                                // Inform everyone involved that the battle was declined
                                val playerTeam = BattleRegistry.playerToTeam[player.uuid]
                                BattleRegistry.removeChallenge(targetedTeam.teamID, existingChallenge.challengeId, true)

                                // Send messages to player team
                                playerTeam?.teamPlayersUUID?.forEach { uuid ->
                                    if(uuid == player.uuid) {
                                        uuid.getPlayer()?.sendMessage(lang("challenge.multibattle.decline.sender.personal", targetedEntity.name).yellow())
                                    } else {
                                        uuid.getPlayer()?.sendMessage(lang("challenge.multibattle.decline.sender", player.name, targetedEntity.name).yellow())
                                    }
                                }

                                // Send messages to targeted team
                                targetedTeam.teamPlayersUUID.forEach { uuid ->
                                    uuid.getPlayer()?.sendMessage(lang("challenge.multibattle.decline.receiver", player.name).yellow())
                                }
                            }
                        } else {
                            targetedEntity.sendMessage(lang("challenge.decline.receiver", player.name).yellow())
                            player.sendMessage(lang("challenge.decline.sender", targetedEntity.name).yellow())
                            BattleRegistry.removeChallenge(targetedEntity.uuid, existingChallenge.challengeId)
                        }

                    }
                }
            }
            else -> {
                // Unrecognized challenge target. NPCs will probably go here.
            }
        }
    }

}