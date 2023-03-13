/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.trade

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText

class TradeStartedPacket(val traderId: UUID, val traderName: MutableText) : NetworkPacket<TradeStartedPacket> {
    companion object {
        val ID = cobblemonResource("trade_started")
        fun decode(buffer: PacketByteBuf) = TradeStartedPacket(buffer.readUuid(), buffer.readText().copy())
    }

    override val id = ID
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(traderId)
        buffer.writeText(traderName)
    }
}