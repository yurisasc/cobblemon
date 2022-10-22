/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.server.battle

import com.cablemc.pokemod.common.api.net.NetworkPacket
import com.cablemc.pokemod.common.battles.ShowdownActionResponse
import com.cablemc.pokemod.common.net.IntSize
import com.cablemc.pokemod.common.util.readSizedInt
import com.cablemc.pokemod.common.util.writeSizedInt
import java.util.UUID
import net.minecraft.network.PacketByteBuf

class BattleSelectActionsPacket() : NetworkPacket {
    lateinit var battleId: UUID
    lateinit var showdownActionResponses: List<ShowdownActionResponse>

    constructor(battleId: UUID, showdownActionResponses: List<ShowdownActionResponse>): this() {
        this.battleId = battleId
        this.showdownActionResponses = showdownActionResponses
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(battleId)
        buffer.writeSizedInt(IntSize.U_BYTE, showdownActionResponses.size)
        showdownActionResponses.forEach { it.saveToBuffer(buffer) }
    }

    override fun decode(buffer: PacketByteBuf) {
        battleId = buffer.readUuid()
        val responses = mutableListOf<ShowdownActionResponse>()
        repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) {
            responses.add(ShowdownActionResponse.loadFromBuffer(buffer))
        }
        showdownActionResponses = responses
    }

}