package com.cablemc.pokemoncobbled.common.net.messages.server.starter

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf

/**
 * Packet sent by the client when they are requesting to choose a starter. The response
 * should probably be a packet instructing the starter screen to open.
 *
 * @author Hiroku
 * @since August 1st, 2022
 */
class RequestStarterScreenPacket internal constructor() : NetworkPacket {
    override fun encode(buffer: PacketByteBuf) {}
    override fun decode(buffer: PacketByteBuf) {}
}