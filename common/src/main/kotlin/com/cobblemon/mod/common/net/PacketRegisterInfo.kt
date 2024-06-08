/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.net.PacketHandler
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

/**
 * A generic wrapping of packet information for registration.
 *
 * @author Apion, Hiroku
 * @since June 7th, 2024
 */
data class PacketRegisterInfo<T : NetworkPacket<T>>(
    val id: Identifier,
    val decoder: (RegistryByteBuf) -> T,
    val handler: PacketHandler<T>
) {
    val payloadId = CustomPayload.Id<T>(id)
    val codec = PacketCodec.of(
        { packet, buf -> packet.encode(buf) },
        decoder
    )
}