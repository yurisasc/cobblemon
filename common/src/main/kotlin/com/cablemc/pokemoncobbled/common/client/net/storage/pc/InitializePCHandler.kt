package com.cablemc.pokemoncobbled.common.client.net.storage.pc

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.client.storage.ClientPC
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.InitializePCPacket

object InitializePCHandler : ClientPacketHandler<InitializePCPacket> {
    override fun invokeOnClient(packet: InitializePCPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.storage.pcStores[packet.storeID] = ClientPC(packet.storeID, packet.boxCount)
    }
}