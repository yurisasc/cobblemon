package com.cablemc.pokemoncobbled.client.net.storage.party

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.InitializePartyPacket
import net.minecraftforge.fmllegacy.network.NetworkEvent

object InitializePartyHandler : ClientPacketHandler<InitializePartyPacket> {
    override fun invokeOnClient(packet: InitializePartyPacket, ctx: NetworkEvent.Context) {
        PokemonCobbledClient.storage.createParty(packet.isThisPlayerParty, packet.uuid, packet.slots)
    }
}