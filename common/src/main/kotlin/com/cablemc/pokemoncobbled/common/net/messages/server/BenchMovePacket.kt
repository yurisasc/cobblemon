package com.cablemc.pokemoncobbled.common.net.messages.server

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.FriendlyByteBuf
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

    override fun encode(buffer: FriendlyByteBuf) {
        buffer.writeBoolean(isParty)
        buffer.writeUUID(uuid)
        buffer.writeUtf(oldMove)
        buffer.writeUtf(newMove)
    }

    override fun decode(buffer: FriendlyByteBuf) {
        isParty = buffer.readBoolean()
        uuid = buffer.readUUID()
        oldMove = buffer.readUtf()
        newMove = buffer.readUtf()
    }
}