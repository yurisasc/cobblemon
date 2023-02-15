/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.starter

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.storage.player.PlayerData
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.network.PacketByteBuf

/**
 * Packet to update the general player data on the client (which is just starter information).
 *
 * @author Hiroku
 * @since August 1st, 2022
 */
class SetClientPlayerDataPacket(val promptStarter: Boolean, val starterLocked: Boolean, val starterSelected: Boolean, val starterUUID: UUID?) : NetworkPacket<SetClientPlayerDataPacket> {

    override val id = ID

    constructor(playerData: PlayerData): this(!playerData.starterPrompted || !Cobblemon.starterConfig.promptStarterOnceOnly, playerData.starterLocked, playerData.starterSelected, playerData.starterUUID)

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(promptStarter)
        buffer.writeBoolean(starterLocked)
        buffer.writeBoolean(starterSelected)
        val starterUUID = starterUUID
        buffer.writeNullable(starterUUID) { pb, value -> pb.writeUuid(value) }
    }

    companion object {
        val ID = cobblemonResource("set_client_playerdata")
        fun decode(buffer: PacketByteBuf): SetClientPlayerDataPacket {
            val promptStarter = buffer.readBoolean()
            val starterLocked = buffer.readBoolean()
            val starterSelected = buffer.readBoolean()
            val starterUUID = buffer.readNullable { it.readUuid() }
            return SetClientPlayerDataPacket(promptStarter, starterLocked, starterSelected, starterUUID)
        }
    }
}