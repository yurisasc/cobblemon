package com.cablemc.pokemoncobbled.forge.common.net.messages.server

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.FriendlyByteBuf

class RequestMoveSwapPacket(): NetworkPacket {

    var move1 = -1
    var move2 = -1
    var slot = -1

    constructor(
        move1: Int,
        move2: Int,
        slot: Int
    ): this() {
        this.move1 = move1
        this.move2 = move2
        this.slot = slot
    }

    override fun encode(buffer: FriendlyByteBuf) {
        buffer.writeInt(move1)
        buffer.writeInt(move2)
        buffer.writeInt(slot)
    }

    override fun decode(buffer: FriendlyByteBuf) {
        move1 = buffer.readInt()
        move2 = buffer.readInt()
        slot = buffer.readInt()
    }
}