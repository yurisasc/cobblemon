package com.cablemc.pokemoncobbled.common.api.net

import net.minecraft.network.FriendlyByteBuf

/*
 * Simple packet interface to make a more traditional layout for netcode
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
interface NetworkPacket {
    fun encode(buffer: FriendlyByteBuf)
    fun decode(buffer: FriendlyByteBuf)
}