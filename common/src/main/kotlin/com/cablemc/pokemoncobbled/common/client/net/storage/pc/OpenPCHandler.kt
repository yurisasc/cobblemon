package com.cablemc.pokemoncobbled.common.client.net.storage.pc

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.OpenPCPacket

object OpenPCHandler : ClientPacketHandler<OpenPCPacket> {
    override fun invokeOnClient(packet: OpenPCPacket, ctx: CobbledNetwork.NetworkContext) {
        val pc = PokemonCobbledClient.storage.pcStores[packet.storeID] ?: return
        // TODO open a PC GUI targeting `pc`
    }
}