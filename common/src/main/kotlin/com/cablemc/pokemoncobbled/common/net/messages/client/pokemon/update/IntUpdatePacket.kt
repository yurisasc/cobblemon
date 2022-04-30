package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf

/**
 * A specific type of update for a Pokémon which updates a single integer value.
 *
 * This can be used for anything upper-bounded by an int, including shorts and bytes.
 *
 * @author Hiroku
 * @since November 28th, 2021
 */
abstract class IntUpdatePacket : SingleUpdatePacket<Int>(1) {
    abstract fun getSize(): IntSize

    override fun encodeValue(buffer: PacketByteBuf, value: Int) {
        buffer.writeSizedInt(getSize(), value)
    }

    override fun decodeValue(buffer: PacketByteBuf): Int {
        return buffer.readSizedInt(getSize())
    }
}