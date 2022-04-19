package com.cablemc.pokemoncobbled.common.net.serverhandling.storage

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.server.BenchMovePacket
import com.cablemc.pokemoncobbled.common.util.party
import com.cablemc.pokemoncobbled.common.util.runOnServer

object BenchMoveHandler : PacketHandler<BenchMovePacket> {
    override fun invoke(packet: BenchMovePacket, ctx: CobbledNetwork.NetworkContext) {
        val player = ctx.player ?: return
        runOnServer {
            val pokemonStore: PokemonStore<out StorePosition> = if (packet.isParty) {
                player.party()
            } else {
                TODO("PC storage #19")
            }

            val pokemon = pokemonStore[packet.uuid] ?: return@runOnServer
            val oldMove = Moves.getByName(packet.oldMove) ?: return@runOnServer
            val newMove = Moves.getByName(packet.newMove) ?: return@runOnServer

            if (pokemon.moveSet.none { it.template == oldMove } || pokemon.moveSet.any { it.template == newMove }) {
                // Something inconsistent in the information they're sending, better give them an update on their moveset
                // in case they're just out of date somehow.
                pokemon.moveSet.update()
                return@runOnServer
            }

            if (newMove !in pokemon.allAccessibleMoves) {
                return@runOnServer
            }

            pokemon.exchangeMove(oldMove, newMove)
        }
    }
}