package com.cablemc.pokemoncobbled.common.api.net.serializers

import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.network.PacketByteBuf

object StringSetDataSerializer : TrackedDataHandler<Set<String>> {
    override fun write(buffer: PacketByteBuf, set: Set<String>) {
        buffer.writeSizedInt(IntSize.U_BYTE, set.size)
        set.forEach(buffer::writeString)
    }

    override fun read(buffer: PacketByteBuf): Set<String> {
        val set = mutableSetOf<String>()
        repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) {
            set.add(buffer.readString())
        }
        return set
    }

    override fun copy(set: Set<String>) = set.toSet()

}