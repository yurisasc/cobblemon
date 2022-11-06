package com.cobblemon.mod.common.api.net

import net.minecraft.network.PacketByteBuf

/**
 * Encodes an object of type [T] to a [PacketByteBuf].
 *
 * @param T The type of the object being encoded.
 *
 * @author Licious
 * @since November 6th, 2022
 */
interface ContextedEncodable<T> {

    /**
     * Writes to the given buffer.
     *
     * @param buffer The [PacketByteBuf] being written to.
     * @param data The object of type [T] being written.
     */
    fun encode(buffer: PacketByteBuf, data: T)

}