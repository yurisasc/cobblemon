package com.cablemc.pokemoncobbled.forge.client.net.storage.party

import com.cablemc.pokemoncobbled.forge.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.forge.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.storage.party.SwapPartyPokemonPacket
import net.minecraftforge.network.NetworkEvent

object SwapPartyPokemonHandler : ClientPacketHandler<SwapPartyPokemonPacket> {
    override fun invokeOnClient(packet: SwapPartyPokemonPacket, ctx: NetworkEvent.Context) {
        PokemonCobbledClient.storage.swapInParty(packet.storeID, packet.pokemonID1, packet.pokemonID2)
    }
}