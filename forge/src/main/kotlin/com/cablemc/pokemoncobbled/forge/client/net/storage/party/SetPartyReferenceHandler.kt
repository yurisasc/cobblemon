package com.cablemc.pokemoncobbled.forge.client.net.storage.party

import com.cablemc.pokemoncobbled.forge.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.forge.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.storage.party.SetPartyReferencePacket
import net.minecraftforge.network.NetworkEvent

object SetPartyReferenceHandler : ClientPacketHandler<SetPartyReferencePacket> {
    override fun invokeOnClient(packet: SetPartyReferencePacket, ctx: NetworkEvent.Context) {
        PokemonCobbledClient.storage.setPartyStore(packet.storeID)
    }
}