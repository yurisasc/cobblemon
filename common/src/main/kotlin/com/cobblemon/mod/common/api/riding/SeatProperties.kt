/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding

import com.cobblemon.mod.common.api.net.Encodable
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Vec3d

/**
 * Seat Properties are responsible for the base information that would then be used to construct a Seat on an entity.
 */
data class SeatProperties(
    val locator: String = "seat1",
    val offset: Vec3d = Vec3d.ZERO
) : Encodable {
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeString(locator)
        buffer.writeDouble(this.offset.x)
        buffer.writeDouble(this.offset.y)
        buffer.writeDouble(this.offset.z)
    }

    companion object {
        fun decode(buffer: PacketByteBuf) : SeatProperties {
            return SeatProperties(
                buffer.readString(),
                Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
            )
        }
    }
}
