package com.cablemc.pokemod.common.api.net

import net.minecraft.network.PacketByteBuf

/**
 * Represents an object that can be encoded to a [PacketByteBuf].
 *
 * @author Licious
 * @since October 14th, 2022
 */
interface Encodable {

    /**
     * Writes this instance to the given buffer.
     *
     * @param buffer The [PacketByteBuf] being written to.
     */
    fun encode(buffer: PacketByteBuf)

}