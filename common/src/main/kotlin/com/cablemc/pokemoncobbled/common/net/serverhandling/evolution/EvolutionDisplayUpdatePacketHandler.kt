package com.cablemc.pokemoncobbled.common.net.serverhandling.evolution

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.server.pokemon.update.evolution.EvolutionDisplayUpdatePacket
import com.cablemc.pokemoncobbled.common.util.party
import com.cablemc.pokemoncobbled.common.util.runOnServer

class EvolutionDisplayUpdatePacketHandler<T : EvolutionDisplayUpdatePacket> : PacketHandler<T> {

    override fun invoke(packet: T, ctx: CobbledNetwork.NetworkContext) {
        runOnServer {
            // We only accept party evolutions
            ctx.player?.party()?.get(packet.pokemonID)?.let { pokemon -> packet.applyToPokemon(pokemon) }
        }
    }

}