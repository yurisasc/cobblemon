package com.cablemc.pokemoncobbled.client.net.storage.party

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SetPartyReferencePacket
import net.minecraftforge.network.NetworkEvent

object SetPartyReferenceHandler : ClientPacketHandler<SetPartyReferencePacket> {
    override fun invokeOnClient(packet: SetPartyReferencePacket, ctx: NetworkEvent.Context) {
        PokemonCobbledClient.storage.setPartyStore(packet.storeID)
    }
}