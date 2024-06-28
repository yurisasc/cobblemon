/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.components

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.resources.ResourceLocation

data class HeldItemCapableComponent(val item: ResourceLocation) {
    companion object {
        val CODEC: Codec<HeldItemCapableComponent> = ResourceLocation.CODEC.xmap(
            { HeldItemCapableComponent(it) },
            { it.item }
        )

        val PACKET_CODEC: PacketCodec<ByteBuf, HeldItemCapableComponent> = PacketCodecs.codec(CODEC)
    }
}
