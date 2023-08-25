/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.npc

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

/**
 * Packet sent to the client to make them begin an animation based on some kind of label.
 *
 * @author Hiroku
 * @since August 25th, 2023
 */
class PlayNPCAnimationPacket(val entityId: Int, val animationType: String) : NetworkPacket<PlayNPCAnimationPacket> {
    companion object {
        val ID = cobblemonResource("play_npc_animation")
        fun decode(buffer: PacketByteBuf) = PlayNPCAnimationPacket(buffer.readInt(), buffer.readString())
    }

    override val id = ID
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeInt(entityId)
        buffer.writeString(animationType)
    }
}