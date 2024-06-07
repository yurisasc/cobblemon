/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.server.callback.move

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import io.netty.buffer.ByteBuf
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Packet sent to the server when the player closed the move selection GUI.
 *
 * @author Hiroku
 * @since July 1st, 2023
 */
class MoveSelectCancelledPacket(val uuid: UUID) : NetworkPacket<MoveSelectCancelledPacket> {
    companion object {
        val ID = cobblemonResource("move_select_cancelled")
        fun decode(buffer: PacketByteBuf) = MoveSelectCancelledPacket(buffer.readUuid())
    }

    override val id = ID
    override fun encode(buffer: ByteBuf) {
        buffer.writeUuid(uuid)
    }
}