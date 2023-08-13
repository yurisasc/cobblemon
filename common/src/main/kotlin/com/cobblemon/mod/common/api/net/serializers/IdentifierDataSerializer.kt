package com.cobblemon.mod.common.api.net.serializers

import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object IdentifierDataSerializer : TrackedDataHandler<Identifier> {
    override fun write(buffer: PacketByteBuf, identifier: Identifier) {
        buffer.writeString(identifier.namespace)
        buffer.writeString(identifier.path)
    }

    override fun read(buffer: PacketByteBuf) = Identifier(buffer.readString(), buffer.readString())
    override fun copy(identifier: Identifier) = Identifier(identifier.namespace, identifier.path)
}