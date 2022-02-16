package com.cablemc.pokemoncobbled.client.net.storage.party

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SetPartyPokemonPacket
import net.minecraftforge.network.NetworkEvent

object SetPartyPokemonHandler : ClientPacketHandler<SetPartyPokemonPacket> {
    override fun invokeOnClient(packet: SetPartyPokemonPacket, ctx: NetworkEvent.Context) {
        PokemonCobbledClient.storage.setPartyPokemon(packet.storeID, packet.storePosition, packet.pokemon)
    }
}