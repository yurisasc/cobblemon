/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.battle

import com.cablemc.pokemod.common.api.net.NetworkPacket
import com.cablemc.pokemod.common.net.IntSize
import com.cablemc.pokemod.common.util.readSizedInt
import com.cablemc.pokemod.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.Text

/**
 * Sends messages to add to the battle message queue on the client.
 *
 * @author Hiroku
 * @since May 22nd, 2022
 */
class BattleMessagePacket() : NetworkPacket {
    val messages = mutableListOf<Text>()
    constructor(vararg messages: Text): this() {
        this.messages.addAll(messages)
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeSizedInt(IntSize.U_BYTE, messages.size)
        messages.forEach(buffer::writeText)
    }

    override fun decode(buffer: PacketByteBuf) {
        repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) {
            messages.add(buffer.readText())
        }
    }
}