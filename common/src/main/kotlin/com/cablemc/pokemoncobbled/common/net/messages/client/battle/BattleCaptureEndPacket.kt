package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf

/**
 * Tells the participants that the capture on the specified Pokémon has finished.
 *
 * @author Hiroku
 * @since July 2nd, 2022
 */
class BattleCaptureEndPacket() : NetworkPacket {
    lateinit var targetPNX: String
    var succeeded = true

    constructor(targetPNX: String, succeeded: Boolean): this() {
        this.targetPNX = targetPNX
        this.succeeded = succeeded
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeString(targetPNX)
        buffer.writeBoolean(succeeded)
    }

    override fun decode(buffer: PacketByteBuf) {
        targetPNX = buffer.readString()
        succeeded = buffer.readBoolean()
    }
}