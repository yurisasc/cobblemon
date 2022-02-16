package com.cablemc.pokemoncobbled.client.net.storage.party

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.MovePartyPokemonPacket
import net.minecraftforge.network.NetworkEvent

object MovePartyPokemonHandler : ClientPacketHandler<MovePartyPokemonPacket> {
    override fun invokeOnClient(packet: MovePartyPokemonPacket, ctx: NetworkEvent.Context) {
        PokemonCobbledClient.storage.moveInParty(packet.storeID, packet.pokemonID, packet.newPosition)
    }
}