package com.cablemc.pokemoncobbled.client.net.pokemon.update

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.PokemonUpdatePacket
import net.minecraftforge.fmllegacy.network.NetworkEvent

class SingleUpdatePacketHandler<T : PokemonUpdatePacket> : ClientPacketHandler<T> {
    override fun invokeOnClient(packet: T, ctx: NetworkEvent.Context) {
        val pokemon = PokemonCobbledClient.storage.locatePokemon(packet.storeID, packet.pokemonID)
            ?: return // Ignore the update, it's not for a Pokémon we know about.
        packet.applyToPokemon(pokemon)
    }
}