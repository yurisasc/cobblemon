package com.cablemc.pokemoncobbled.common.net.messages.server

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf
import java.util.UUID

class BenchMovePacket() : NetworkPacket {
    var isParty = true
    lateinit var uuid: UUID
    lateinit var oldMove: String
    lateinit var newMove: String

    constructor(isParty: Boolean, uuid: UUID, oldMove: MoveTemplate, newMove: MoveTemplate): this() {
        this.isParty = isParty
        this.uuid = uuid
        this.oldMove = oldMove.name
        this.newMove = newMove.name
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(isParty)
        buffer.writeUuid(uuid)
        buffer.writeString(oldMove)
        buffer.writeString(newMove)
    }

    override fun decode(buffer: PacketByteBuf) {
        isParty = buffer.readBoolean()
        uuid = buffer.readUuid()
        oldMove = buffer.readString()
        newMove = buffer.readString()
    }
}