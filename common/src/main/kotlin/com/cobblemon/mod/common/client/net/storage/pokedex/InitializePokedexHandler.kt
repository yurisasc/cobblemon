package com.cobblemon.mod.common.client.net.storage.pokedex

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.net.ClientPacketHandler
import com.cobblemon.mod.common.net.messages.client.storage.pc.InitializePokedexPacket

class InitializePokedexHandler : ClientPacketHandler<InitializePokedexPacket> {
    override fun invokeOnClient(packet: InitializePokedexPacket, ctx: CobblemonNetwork.NetworkContext) {
        CobblemonClient.storage.createPokedex(packet.isThisPlayerPokedex, packet.uuid)
    }
}