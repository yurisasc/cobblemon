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
import com.cobblemon.mod.common.util.readUuid
import com.cobblemon.mod.common.util.writeUuid
import net.minecraft.network.RegistryByteBuf
import java.util.UUID

/**
 * Packet fired to tell the client that a trade offer expired.
 *
 * @author Hiroku
 * @since March 11th, 2023
 */
class TradeOfferExpiredPacket(val tradeOfferId: UUID) : NetworkPacket<TradeOfferExpiredPacket> {
    companion object {
        val ID = cobblemonResource("trade_offer_expired")
        fun decode(buffer: RegistryByteBuf) = TradeOfferExpiredPacket(buffer.readUuid())
    }

    override val id = ID
    override fun encode(buffer: RegistryByteBuf) {
        buffer.writeUuid(tradeOfferId)
    }
}