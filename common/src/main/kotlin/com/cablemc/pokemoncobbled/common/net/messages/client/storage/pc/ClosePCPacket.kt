package com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Notifies a player that they must close their PC GUI. If the [storeID] property is non-null, then
 * it will only close the PC GUI if it was opened for that specific PC.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.storage.pc.ClosePCHandler]
 *
 * @author Hiroku
 * @since June 20th, 2022
 */
class ClosePCPacket() : NetworkPacket {
    var storeID: UUID? = null

    constructor(storeID: UUID?): this() {
        this.storeID = storeID
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(storeID != null)
        storeID?.let(buffer::writeUuid)
    }

    override fun decode(buffer: PacketByteBuf) {
        if (buffer.readBoolean()) {
            storeID = buffer.readUuid()
        }
    }
}