package com.cablemc.pokemoncobbled.common.api.serialization

import net.minecraft.network.PacketByteBuf

/**
 * A Netty serializer.
 *
 * @author Licious
 * @since June 27th, 2022
 */
interface BufferSerializer {

    /**
     * Saves this object into a [PacketByteBuf].
     *
     * @param buffer The [PacketByteBuf] the data will be written to.
     * @param toClient If the resulting packet will be client-bound.
     */
    fun saveToBuffer(buffer: PacketByteBuf, toClient: Boolean)

    /**
     * Loads the data from the given [PacketByteBuf].
     *
     * @param buffer The [PacketByteBuf] with the received data.
     */
    fun loadFromBuffer(buffer: PacketByteBuf)

}