package com.cablemc.pokemoncobbled.common.client.net.storage.party

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SetPartyPokemonPacket

object SetPartyPokemonHandler : ClientPacketHandler<SetPartyPokemonPacket> {
    override fun invokeOnClient(packet: SetPartyPokemonPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.storage.setPartyPokemon(packet.storeID, packet.storePosition, packet.pokemon)
    }
}