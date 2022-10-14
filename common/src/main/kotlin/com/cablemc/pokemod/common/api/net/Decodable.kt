package com.cablemc.pokemod.common.api.net

import net.minecraft.network.PacketByteBuf

interface Decodable {

    /**
     * Reads an updates this instance based on the given buffer.
     *
     * @param buffer The [PacketByteBuf] being read from.
     */
    fun decode(buffer: PacketByteBuf)

}