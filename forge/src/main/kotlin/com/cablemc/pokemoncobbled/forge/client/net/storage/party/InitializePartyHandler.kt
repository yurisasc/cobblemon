package com.cablemc.pokemoncobbled.forge.client.net.storage.party

import com.cablemc.pokemoncobbled.forge.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.forge.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.storage.party.InitializePartyPacket
import net.minecraftforge.network.NetworkEvent

object InitializePartyHandler : ClientPacketHandler<InitializePartyPacket> {
    override fun invokeOnClient(packet: InitializePartyPacket, ctx: NetworkEvent.Context) {
        PokemonCobbledClient.storage.createParty(packet.isThisPlayerParty, packet.uuid, packet.slots)
    }
}