package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf

class BattleHealthChangePacket() : NetworkPacket {
    lateinit var pnx: String
    var newHealthRatio = 0F

    constructor(pnx: String, newHealthRatio: Float): this() {
        this.pnx = pnx
        this.newHealthRatio = newHealthRatio
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeString(pnx)
        buffer.writeFloat(newHealthRatio)
    }

    override fun decode(buffer: PacketByteBuf) {
        pnx = buffer.readString()
        newHealthRatio = buffer.readFloat()
    }
}