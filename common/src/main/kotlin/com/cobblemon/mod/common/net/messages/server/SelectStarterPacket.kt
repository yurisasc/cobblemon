/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.server

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class SelectStarterPacket(val categoryId: Identifier, val selected: Int) : NetworkPacket<SelectStarterPacket> {
    override val id = ID
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeIdentifier(this.categoryId)
        buffer.writeInt(this.selected)
    }

    companion object {
        val ID = cobblemonResource("select_starter")
        fun decode(buffer: PacketByteBuf): SelectStarterPacket = SelectStarterPacket(buffer.readIdentifier(), buffer.readInt())
    }
}