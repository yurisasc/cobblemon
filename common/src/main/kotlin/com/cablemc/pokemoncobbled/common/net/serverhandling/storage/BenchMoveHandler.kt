package com.cablemc.pokemoncobbled.common.net.serverhandling.storage

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.pc.link.PCLinkManager
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.ClosePCPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.BenchMovePacket
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketHandler
import com.cablemc.pokemoncobbled.common.util.party
import net.minecraft.server.network.ServerPlayerEntity

object BenchMoveHandler : ServerPacketHandler<BenchMovePacket> {
    override fun invokeOnServer(packet: BenchMovePacket, ctx: CobbledNetwork.NetworkContext, player: ServerPlayerEntity) {
        val pokemonStore: PokemonStore<*> = if (packet.isParty) {
            player.party()
        } else {
            PCLinkManager.getPC(player) ?: return run { ClosePCPacket().sendToPlayer(player) }
        }

        val pokemon = pokemonStore[packet.uuid] ?: return
        val oldMove = Moves.getByName(packet.oldMove) ?: return
        val newMove = Moves.getByName(packet.newMove) ?: return

        if (pokemon.moveSet.none { it.template == oldMove } || pokemon.moveSet.any { it.template == newMove }) {
            // Something inconsistent in the information they're sending, better give them an update on their moveset
            // in case they're just out of date somehow.
            pokemon.moveSet.update()
            return
        }

        if (newMove !in pokemon.allAccessibleMoves) {
            LOGGER.warn("${player.name} tried to bench ${oldMove.name} for ${newMove.name} but it doesn't have ${newMove.name} learned. Could be a hacker!")
            return
        }

        pokemon.exchangeMove(oldMove, newMove)
    }
}