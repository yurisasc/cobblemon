package com.cablemc.pokemoncobbled.common.api.net.serializers

import com.cablemc.pokemoncobbled.common.entity.PoseType
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.network.PacketByteBuf

object PoseTypeDataSerializer : TrackedDataHandler<PoseType> {
    override fun read(buf: PacketByteBuf) = PoseType.values()[buf.readInt()]
    override fun copy(value: PoseType) = value
    override fun write(buf: PacketByteBuf, value: PoseType) {
        buf.writeInt(value.ordinal)
    }
}