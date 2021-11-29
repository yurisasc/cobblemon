package com.cablemc.pokemoncobbled.common.net.messages.client.storage

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.FriendlyByteBuf
import java.util.UUID

class InitializePartyPacket() : NetworkPacket {
    var isThisPlayerParty: Boolean = false
    var uuid = UUID.randomUUID()
    var slots: Int = 6

    constructor(isThisPlayerParty: Boolean, uuid: UUID, slots: Int): this() {
        this.isThisPlayerParty = isThisPlayerParty
        this.uuid = uuid
        this.slots = slots
    }

    override fun encode(buffer: FriendlyByteBuf) {
        buffer.writeBoolean(isThisPlayerParty)
        buffer.writeUUID(uuid)
        buffer.writeByte(slots)
    }

    override fun decode(buffer: FriendlyByteBuf) {
        isThisPlayerParty = buffer.readBoolean()
        uuid = buffer.readUUID()
        slots = buffer.readUnsignedByte().toInt()
    }
}