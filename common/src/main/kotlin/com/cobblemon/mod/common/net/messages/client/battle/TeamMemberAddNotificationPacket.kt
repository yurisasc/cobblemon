/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.battle

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText

/**
 * Packet sent when a player joins or leaves a team that the client is currently a member of.
 *
 * Handled by [com.cobblemon.mod.common.client.net.battle.TeamRequestNotificationHandler].
 *
 * @author JazzMcNade
 * @since April 9th, 2024
 */
class TeamMemberAddNotificationPacket(
        val teamMemberUUID: UUID,
        val teamMemberName: MutableText,
): NetworkPacket<TeamMemberAddNotificationPacket> {
    override val id = ID
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(teamMemberUUID)
        buffer.writeText(teamMemberName)
    }

    companion object {
        val ID = cobblemonResource("team_member_add_notification")
        fun decode(buffer: PacketByteBuf) = TeamMemberAddNotificationPacket(buffer.readUuid(), buffer.readText().copy())
    }
}