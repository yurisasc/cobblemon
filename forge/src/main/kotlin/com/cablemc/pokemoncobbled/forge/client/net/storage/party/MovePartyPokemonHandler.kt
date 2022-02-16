package com.cablemc.pokemoncobbled.forge.client.net.storage.party

import com.cablemc.pokemoncobbled.forge.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.forge.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.storage.party.MovePartyPokemonPacket
import net.minecraftforge.network.NetworkEvent

object MovePartyPokemonHandler : ClientPacketHandler<MovePartyPokemonPacket> {
    override fun invokeOnClient(packet: MovePartyPokemonPacket, ctx: NetworkEvent.Context) {
        PokemonCobbledClient.storage.moveInParty(packet.storeID, packet.pokemonID, packet.newPosition)
    }
}