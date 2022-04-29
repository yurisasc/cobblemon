package com.cablemc.pokemoncobbled.common.api.net

import net.minecraft.network.PacketByteBuf

/*
 * Simple packet interface to make a more traditional layout for netcode
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
interface NetworkPacket {
    fun encode(buffer: PacketByteBuf)
    fun decode(buffer: PacketByteBuf)
}