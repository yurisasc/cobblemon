package com.cablemc.pokemoncobbled.common.client.net.pokemon.update

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.PokemonUpdatePacket

class SingleUpdatePacketHandler<T : PokemonUpdatePacket> : ClientPacketHandler<T> {
    override fun invokeOnClient(packet: T, ctx: CobbledNetwork.NetworkContext) {
        val pokemon = PokemonCobbledClient.storage.locatePokemon(packet.storeID, packet.pokemonID)
            ?: return // Ignore the update, it's not for a Pokémon we know about.
        packet.applyToPokemon(pokemon)
    }
}