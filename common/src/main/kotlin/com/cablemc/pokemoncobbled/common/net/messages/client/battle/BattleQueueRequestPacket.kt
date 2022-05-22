package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.battles.ShowdownActionRequest
import net.minecraft.network.PacketByteBuf

class BattleQueueRequestPacket(): NetworkPacket {
    lateinit var request: ShowdownActionRequest
    constructor(request: ShowdownActionRequest): this() {
        this.request = request
    }
    override fun encode(buffer: PacketByteBuf) {
        TODO("Not yet implemented")
    }

    override fun decode(buffer: PacketByteBuf) {
        TODO("Not yet implemented")
    }
}