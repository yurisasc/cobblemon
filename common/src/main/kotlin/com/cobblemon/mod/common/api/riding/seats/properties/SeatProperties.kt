/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.seats.properties

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.riding.seats.Seat
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.codec.VECTOR3F_CODEC
import com.cobblemon.mod.common.util.toVec3d
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Vec3d

/**
 * Seat Properties are responsible for the base information that would then be used to construct a Seat on an entity.
 *
 * @since 1.5.0
 */
data class SeatProperties(
    val driver: Boolean = false,
    val offset: Vec3d = Vec3d.ZERO
) : Encodable {

    fun create(mount: PokemonEntity): Seat {
        return Seat(mount, this)
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(this.driver)
        buffer.writeDouble(this.offset.x)
        buffer.writeDouble(this.offset.y)
        buffer.writeDouble(this.offset.z)
    }

    companion object {

        fun decode(buffer: PacketByteBuf) : SeatProperties {
            return SeatProperties(
                buffer.readBoolean(),
                Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
            )
        }

    }

}
