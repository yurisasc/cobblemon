package com.cablemc.pokemoncobbled.common.client.net.storage.party

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.InitializePartyPacket

object InitializePartyHandler : ClientPacketHandler<InitializePartyPacket> {
    override fun invokeOnClient(packet: InitializePartyPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.storage.createParty(packet.isThisPlayerParty, packet.uuid, packet.slots)
    }
}