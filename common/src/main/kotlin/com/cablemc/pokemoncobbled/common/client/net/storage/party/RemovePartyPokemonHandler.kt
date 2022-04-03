package com.cablemc.pokemoncobbled.common.client.net.storage.party

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.RemovePartyPokemonPacket

object RemovePartyPokemonHandler : ClientPacketHandler<RemovePartyPokemonPacket> {
    override fun invokeOnClient(packet: RemovePartyPokemonPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.storage.removeFromParty(packet.storeID, packet.pokemonID)
    }
}