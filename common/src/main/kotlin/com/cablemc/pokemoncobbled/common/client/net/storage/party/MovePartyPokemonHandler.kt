package com.cablemc.pokemoncobbled.common.client.net.storage.party

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.MovePartyPokemonPacket

object MovePartyPokemonHandler : ClientPacketHandler<MovePartyPokemonPacket> {
    override fun invokeOnClient(packet: MovePartyPokemonPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.storage.moveInParty(packet.storeID, packet.pokemonID, packet.newPosition)
    }
}