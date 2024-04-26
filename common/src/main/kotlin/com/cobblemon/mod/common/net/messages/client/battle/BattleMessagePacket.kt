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
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.RegistryByteBuf
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs
import sun.jvm.hotspot.oops.CellTypeState.value

/**
 * Sends messages to add to the battle message queue on the client.
 *
 * @author Hiroku
 * @since May 22nd, 2022
 */
class BattleMessagePacket(val messages: List<Text>) : NetworkPacket<BattleMessagePacket> {

    override val id = ID

    constructor(vararg messages: Text): this(messages.toList())

    override fun encode(buffer: RegistryByteBuf) {
        buffer.writeCollection(this.messages) { pb, value -> TextCodecs.PACKET_CODEC.encode(buffer, value) }
    }

    companion object {
        val ID = cobblemonResource("battle_message")
        fun decode(buffer: PacketByteBuf) = BattleMessagePacket(buffer.readList { TextCodecs.PACKET_CODEC.decode(buffer) })
    }
}