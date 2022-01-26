package com.cablemc.pokemoncobbled.client.net.storage.party

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SwapPartyPokemonPacket
import net.minecraftforge.network.NetworkEvent

object SwapPartyPokemonHandler : ClientPacketHandler<SwapPartyPokemonPacket> {
    override fun invokeOnClient(packet: SwapPartyPokemonPacket, ctx: NetworkEvent.Context) {
        PokemonCobbledClient.storage.swapInParty(packet.storeID, packet.pokemonID1, packet.pokemonID2)
    }
}