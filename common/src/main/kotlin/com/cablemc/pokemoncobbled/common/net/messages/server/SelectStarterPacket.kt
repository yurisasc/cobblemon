package com.cablemc.pokemoncobbled.common.net.messages.server

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf

class SelectStarterPacket internal constructor() : NetworkPacket {

    var categoryName: String = ""
    var selected: Int = -1

    constructor(categoryName: String, selected: Int) : this() {
        this.categoryName = categoryName
        this.selected = selected
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeString(categoryName)
        buffer.writeInt(selected)
    }

    override fun decode(buffer: PacketByteBuf) {
        categoryName = buffer.readString()
        selected = buffer.readInt()
    }
}