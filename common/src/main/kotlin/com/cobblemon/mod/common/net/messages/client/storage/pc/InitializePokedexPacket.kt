package com.cobblemon.mod.common.net.messages.client.storage.pc

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.net.IntSize
import com.cobblemon.mod.common.util.readSizedInt
import com.cobblemon.mod.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf
import java.util.*

class InitializePokedexPacket(
    /** Whether this should be set as the player's party for rendering immediately. */
    var isThisPlayerPokedex: Boolean, uuid: UUID
) : NetworkPacket {
    /** The UUID of the pokedex storage. Does not need to be the player's UUID. */
    var uuid = uuid

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(isThisPlayerPokedex)
        buffer.writeUuid(uuid)
    }

    override fun decode(buffer: PacketByteBuf) {
        isThisPlayerPokedex = buffer.readBoolean()
        uuid = buffer.readUuid()
    }
}