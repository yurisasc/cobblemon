package com.cobblemon.mod.common.api.net.serializers

import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/**
 * Data serializer of [Identifier] for DataTracker things.
 *
 * @author Hiroku
 * @since May 22nd, 2023
 */
object IdentifierDataSerializer : TrackedDataHandler<Identifier> {
    override fun copy(value: Identifier) = Identifier(value.namespace, value.path)
    override fun read(buf: PacketByteBuf) = Identifier(buf.readString(), buf.readString())
    override fun write(buf: PacketByteBuf, value: Identifier) {
        buf.writeString(value.namespace)
        buf.writeString(value.path)
    }
}