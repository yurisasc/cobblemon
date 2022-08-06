package com.cablemc.pokemoncobbled.common.net.serverhandling

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.CobbledNetwork.sendPacket
import com.cablemc.pokemoncobbled.common.api.scheduling.after
import com.cablemc.pokemoncobbled.common.api.text.aqua
import com.cablemc.pokemoncobbled.common.api.text.red
import com.cablemc.pokemoncobbled.common.api.text.yellow
import com.cablemc.pokemoncobbled.common.battles.BattleBuilder
import com.cablemc.pokemoncobbled.common.battles.BattleRegistry
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.ChallengeNotificationPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.BattleChallengePacket
import com.cablemc.pokemoncobbled.common.util.lang
import com.cablemc.pokemoncobbled.common.util.party
import com.cablemc.pokemoncobbled.common.util.runOnServer
import com.cablemc.pokemoncobbled.common.util.sendServerMessage
import net.minecraft.server.network.ServerPlayerEntity

object ChallengeHandler : PacketHandler<BattleChallengePacket> {
    override fun invoke(packet: BattleChallengePacket, ctx: CobbledNetwork.NetworkContext) {
        runOnServer {
            val player = ctx.player ?: return@runOnServer
            val targetedEntity = player.world.getEntityById(packet.targetedEntityId) ?: return@runOnServer
            val leadingPokemon = player.party()[packet.selectedPokemonId]?.uuid ?: return@runOnServer

            when (targetedEntity) {
                is PokemonEntity -> {
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
                        player.sendServerMessage(lang("challenge.sender", targetedEntity.name).yellow())
                    }
                }
                else -> {
                    // Unrecognized challenge target. NPCs will probably go here.
                }
            }
        }
    }
}