package com.cablemc.pokemoncobbled.common.client.net.storage.party

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.MoveClientPartyPokemonPacket

object MovePartyPokemonHandler : ClientPacketHandler<MoveClientPartyPokemonPacket> {
    override fun invokeOnClient(packet: MoveClientPartyPokemonPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.storage.moveInParty(packet.storeID, packet.pokemonID, packet.newPosition)
    }
}