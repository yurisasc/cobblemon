package com.cablemc.pokemoncobbled.common.client.net.storage.party

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SetPartyReferencePacket

object SetPartyReferenceHandler : ClientPacketHandler<SetPartyReferencePacket> {
    override fun invokeOnClient(packet: SetPartyReferencePacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.storage.setPartyStore(packet.storeID)
    }
}