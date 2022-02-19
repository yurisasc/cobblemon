package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.server.level.ServerPlayer

interface PokemonCobbledModImplementation {
    /** Only access from the client logical side or death will occur. */
    val client: PokemonCobbledClientImplementation
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
        discriminator: Int,
        toServer: Boolean
    ): CobbledNetwork.PreparedMessage<T>
}