package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
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