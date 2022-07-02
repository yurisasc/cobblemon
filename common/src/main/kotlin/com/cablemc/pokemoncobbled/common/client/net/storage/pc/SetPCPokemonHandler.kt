package com.cablemc.pokemoncobbled.common.client.net.storage.pc

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.SetPCPokemonPacket

object SetPCPokemonHandler : ClientPacketHandler<SetPCPokemonPacket> {
    override fun invokeOnClient(packet: SetPCPokemonPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.storage.setPCPokemon(packet.storeID, packet.storePosition, packet.pokemon)
    }
}