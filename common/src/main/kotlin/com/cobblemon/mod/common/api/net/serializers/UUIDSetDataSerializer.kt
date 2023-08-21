/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.net.serializers

import com.cobblemon.mod.common.net.IntSize
import com.cobblemon.mod.common.util.readSizedInt
import com.cobblemon.mod.common.util.writeSizedInt
import java.util.UUID
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.network.PacketByteBuf

object UUIDSetDataSerializer : TrackedDataHandler<Set<UUID>> {
    override fun write(buffer: PacketByteBuf, set: Set<UUID>) {
        buffer.writeSizedInt(IntSize.U_BYTE, set.size)
        set.forEach(buffer::writeUuid)
    }

    override fun read(buffer: PacketByteBuf): Set<UUID> {
        val set = mutableSetOf<UUID>()
        repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) {
            set.add(buffer.readUuid())
        }
        return set
    }

    override fun copy(set: Set<UUID>) = set.toSet()
}