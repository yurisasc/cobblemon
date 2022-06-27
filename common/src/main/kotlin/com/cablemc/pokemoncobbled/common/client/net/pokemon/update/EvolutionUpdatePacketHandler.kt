package com.cablemc.pokemoncobbled.common.client.net.pokemon.update

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.evolution.EvolutionUpdatePacket

class EvolutionUpdatePacketHandler<T : EvolutionUpdatePacket> : ClientPacketHandler<T> {

    override fun invokeOnClient(packet: T, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.storage.locatePokemon(packet.storeID, packet.pokemonID)?.let { pokemon -> packet.applyToPokemon(pokemon) }
    }

}