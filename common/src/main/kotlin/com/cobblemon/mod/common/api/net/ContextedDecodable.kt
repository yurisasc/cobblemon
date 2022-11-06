package com.cobblemon.mod.common.api.net

import net.minecraft.network.PacketByteBuf

/**
 * Decodes an object of type [T] from a [PacketByteBuf].
 *
 * @param T The type of the object being decoded.
 *
 * @author Licious
 * @since November 6th, 2022
 */
interface ContextedDecodable<T> {

    /**
     * Reads from the given buffer.
     *
     * @param buffer The [PacketByteBuf] being read from.
     * @return The object of type [T].
     */
    fun decode(buffer: PacketByteBuf): T

}