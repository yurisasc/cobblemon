/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.server.battle

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import java.util.UUID

class FleeBattlePacket(val battleId: UUID) : NetworkPacket<FleeBattlePacket> {

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(battleId)
    }

    companion object {
        val ID = cobblemonResource("flee_battle")

        fun decode(buffer: PacketByteBuf): FleeBattlePacket {
            return FleeBattlePacket(buffer.readUuid())
        }
    }

}