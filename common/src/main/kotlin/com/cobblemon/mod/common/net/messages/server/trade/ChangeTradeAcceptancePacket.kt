/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.server.trade

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.readUuid
import com.cobblemon.mod.common.util.writeUuid
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.PacketByteBuf
import java.util.UUID

class ChangeTradeAcceptancePacket(val pokemonOfferId: UUID, val newAcceptance: Boolean) : NetworkPacket<ChangeTradeAcceptancePacket> {
    companion object {
        val ID = cobblemonResource("accept_trade")
        fun decode(buffer: RegistryByteBuf) = ChangeTradeAcceptancePacket(buffer.readUuid(), buffer.readBoolean())
    }

    override val id = ID
    override fun encode(buffer: RegistryByteBuf) {
        buffer.writeUuid(pokemonOfferId)
        buffer.writeBoolean(newAcceptance)
    }
}