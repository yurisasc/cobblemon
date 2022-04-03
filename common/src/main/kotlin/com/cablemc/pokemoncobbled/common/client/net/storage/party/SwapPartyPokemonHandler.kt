package com.cablemc.pokemoncobbled.common.client.net.storage.party

import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SwapPartyPokemonPacket
import com.cablemc.pokemoncobbled.common.CobbledNetwork

object SwapPartyPokemonHandler : ClientPacketHandler<SwapPartyPokemonPacket> {
    override fun invokeOnClient(packet: SwapPartyPokemonPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.storage.swapInParty(packet.storeID, packet.pokemonID1, packet.pokemonID2)
    }
}