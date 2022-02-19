package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.server.level.ServerPlayer

interface PokemonCobbledModImplementation {
    val networkDelegate: NetworkDelegate
}

interface PokemonCobbledClientImplementation {
    fun initialize()
}

interface NetworkDelegate {
    fun sendPacketToPlayer(player: ServerPlayer, packet: NetworkPacket)
    fun sendPacketToServer(packet: NetworkPacket)
    fun <T : NetworkPacket> buildMessage(
        packetClass: Class<T>,
        toServer: Boolean
    ): CobbledNetwork.PreparedMessage<T>
}