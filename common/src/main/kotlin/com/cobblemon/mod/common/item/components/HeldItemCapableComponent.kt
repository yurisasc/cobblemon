package com.cobblemon.mod.common.item.components

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.util.Identifier

data class HeldItemCapableComponent(val item: Identifier) {
    companion object {
        val CODEC: Codec<HeldItemCapableComponent> = Identifier.CODEC.xmap(
            { HeldItemCapableComponent(it) },
            { it.item }
        )

        val PACKET_CODEC: PacketCodec<ByteBuf, HeldItemCapableComponent> = PacketCodecs.codec(CODEC)
    }
}
