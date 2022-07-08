package com.cablemc.pokemoncobbled.common.client.net.storage

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.SwapClientPokemonPacket

object SwapClientPokemonHandler : ClientPacketHandler<SwapClientPokemonPacket> {
    override fun invokeOnClient(packet: SwapClientPokemonPacket, ctx: CobbledNetwork.NetworkContext) {
        if (packet.storeIsParty) {
            PokemonCobbledClient.storage.swapInParty(packet.storeID, packet.pokemonID1, packet.pokemonID2)
        } else {
            PokemonCobbledClient.storage.swapInPC(packet.storeID, packet.pokemonID1, packet.pokemonID2)
        }
    }
}