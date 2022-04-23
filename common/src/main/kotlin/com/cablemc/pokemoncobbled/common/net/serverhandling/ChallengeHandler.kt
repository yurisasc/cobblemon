package com.cablemc.pokemoncobbled.common.net.serverhandling

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.text.red
import com.cablemc.pokemoncobbled.common.battles.BattleBuilder
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.server.ChallengePacket
import com.cablemc.pokemoncobbled.common.util.party
import com.cablemc.pokemoncobbled.common.util.runOnServer
import com.cablemc.pokemoncobbled.common.util.sendServerMessage
import net.minecraft.server.level.ServerPlayer

object ChallengeHandler : PacketHandler<ChallengePacket> {
    override fun invoke(packet: ChallengePacket, ctx: CobbledNetwork.NetworkContext) {
        runOnServer {
            val player = ctx.player ?: return@runOnServer
            val targetedEntity = player.level.getEntity(packet.targetedEntityId) ?: return@runOnServer
            val leadingPokemon = player.party()[packet.selectedPokemonId]?.uuid ?: return@runOnServer

            if (targetedEntity is PokemonEntity) {
                BattleBuilder.pve(player, targetedEntity, leadingPokemon).ifErrored { it.sendTo(player) }
            } else if (targetedEntity is ServerPlayer) {

            } else {
                // Unrecognized challenge target. NPCs will probably go here.
            }
        }
    }
}