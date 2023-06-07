/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.net.serializers

import com.cobblemon.mod.common.api.riding.seats.properties.SeatDTO
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.network.PacketByteBuf

object SeatDataSerializer : TrackedDataHandler<List<SeatDTO>> {

    override fun write(buffer: PacketByteBuf, seats: List<SeatDTO>) {
        buffer.writeCollection(seats) { _, seat -> seat.encode(buffer) }
    }

    override fun read(buffer: PacketByteBuf): List<SeatDTO> {
        return buffer.readList { _ ->
            val dto = SeatDTO()
            dto.decode(buffer)

            return@readList dto
        }
    }

    override fun copy(value: List<SeatDTO>): List<SeatDTO> {
        return value.map { it }
    }

}