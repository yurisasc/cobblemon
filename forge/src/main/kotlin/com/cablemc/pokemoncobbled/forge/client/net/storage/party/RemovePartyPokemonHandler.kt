package com.cablemc.pokemoncobbled.forge.client.net.storage.party

import com.cablemc.pokemoncobbled.forge.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.forge.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.storage.party.RemovePartyPokemonPacket
import net.minecraftforge.network.NetworkEvent

object RemovePartyPokemonHandler : ClientPacketHandler<RemovePartyPokemonPacket> {
    override fun invokeOnClient(packet: RemovePartyPokemonPacket, ctx: NetworkEvent.Context) {
        PokemonCobbledClient.storage.removeFromParty(packet.storeID, packet.pokemonID)
    }
}