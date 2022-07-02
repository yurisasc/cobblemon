package com.cablemc.pokemoncobbled.common.client.net.storage.pc

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.ClosePCPacket

object ClosePCHandler : ClientPacketHandler<ClosePCPacket> {
    override fun invokeOnClient(packet: ClosePCPacket, ctx: CobbledNetwork.NetworkContext) {
        // TODO close the PC GUI if the UUID of the opened PC matches packet.storeID
    }
}