/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.battles.ShowdownActionRequest
import net.minecraft.network.PacketByteBuf

/**
 * Informs the client about the specific battle request that will be made of them at the next upkeep
 * or turn transition. The request isn't immediately displayed, request instructions come significantly
 * before the showdown request that indicates that a choice must be made.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.battle.BattleQueueRequestHandler].
 *
 * @author Hiroku
 * @since May 22nd, 2022
 */
class BattleQueueRequestPacket(): NetworkPacket {
    lateinit var request: ShowdownActionRequest
    constructor(request: ShowdownActionRequest): this() {
        this.request = request
    }
    override fun encode(buffer: PacketByteBuf) {
        request.saveToBuffer(buffer)
    }

    override fun decode(buffer: PacketByteBuf) {
        request = ShowdownActionRequest().loadFromBuffer(buffer)
    }
}