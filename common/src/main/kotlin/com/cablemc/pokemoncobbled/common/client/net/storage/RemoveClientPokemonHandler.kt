package com.cablemc.pokemoncobbled.common.client.net.storage

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.RemoveClientPokemonPacket


object RemoveClientPokemonHandler : ClientPacketHandler<RemoveClientPokemonPacket> {
    override fun invokeOnClient(packet: RemoveClientPokemonPacket, ctx: CobbledNetwork.NetworkContext) {
        if (packet.storeIsParty) {
            PokemonCobbledClient.storage.removeFromParty(packet.storeID, packet.pokemonID)
        } else {
            PokemonCobbledClient.storage.removeFromPC(packet.storeID, packet.pokemonID)
        }
    }
}