package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText

class BattleMessagePacket() : NetworkPacket {
    lateinit var message: MutableText
    constructor(message: MutableText): this() {
        this.message = message
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeText(message)
    }

    override fun decode(buffer: PacketByteBuf) {
        message = buffer.readText().copy()
    }
}