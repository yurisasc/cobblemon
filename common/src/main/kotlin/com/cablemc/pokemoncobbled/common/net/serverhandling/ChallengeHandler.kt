package com.cablemc.pokemoncobbled.common.net.serverhandling

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.api.text.red
import com.cablemc.pokemoncobbled.common.battles.BattleBuilder
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.server.ChallengePacket
import com.cablemc.pokemoncobbled.common.util.party
import com.cablemc.pokemoncobbled.common.util.runOnServer
import net.minecraft.server.network.ServerPlayerEntity

object ChallengeHandler : PacketHandler<ChallengePacket> {
    override fun invoke(packet: ChallengePacket, ctx: CobbledNetwork.NetworkContext) {
        runOnServer {
            val player = ctx.player ?: return@runOnServer
            val targetedEntity = player.world.getEntityById(packet.targetedEntityId) ?: return@runOnServer
            val leadingPokemon = player.party()[packet.selectedPokemonId]?.uuid ?: return@runOnServer

            when (targetedEntity) {
                is PokemonEntity -> {
                    BattleBuilder.pve(player, targetedEntity, leadingPokemon).ifErrored { it.sendTo(player) { it.red() } }
                }
                is ServerPlayerEntity -> {

                }
                else -> {
                    // Unrecognized challenge target. NPCs will probably go here.
                }
            }
        }
    }
}