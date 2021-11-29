package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.FriendlyByteBuf

abstract class IntUpdatePacket : SingleUpdatePacket<Int>(1) {
    abstract fun getSize(): IntSize

    override fun encodeValue(buffer: FriendlyByteBuf, value: Int) {
        buffer.writeSizedInt(getSize(), value)
    }

    override fun decodeValue(buffer: FriendlyByteBuf): Int {
        return buffer.readSizedInt(getSize())
    }
}