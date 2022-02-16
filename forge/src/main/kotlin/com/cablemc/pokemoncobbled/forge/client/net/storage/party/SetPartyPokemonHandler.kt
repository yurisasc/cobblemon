package com.cablemc.pokemoncobbled.forge.client.net.storage.party

import com.cablemc.pokemoncobbled.forge.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.forge.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.storage.party.SetPartyPokemonPacket
import net.minecraftforge.network.NetworkEvent

object SetPartyPokemonHandler : ClientPacketHandler<SetPartyPokemonPacket> {
    override fun invokeOnClient(packet: SetPartyPokemonPacket, ctx: NetworkEvent.Context) {
        PokemonCobbledClient.storage.setPartyPokemon(packet.storeID, packet.storePosition, packet.pokemon)
    }
}