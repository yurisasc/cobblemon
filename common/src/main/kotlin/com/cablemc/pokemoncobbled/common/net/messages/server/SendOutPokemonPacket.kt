package com.cablemc.pokemoncobbled.common.net.messages.server

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.SendOutPokemonHandler
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf

/**
 * Packet sent from the client to the server to send out the Pokémon in the specified
 * slot.
 *
 * Handled by [SendOutPokemonHandler]
 *
 * @author Hiroku
 * @since December 2nd, 2021
 */
class SendOutPokemonPacket() : NetworkPacket {
    var slot = -1

    constructor(slot: Int): this() {
        this.slot = slot
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeSizedInt(IntSize.U_BYTE, slot)
    }

    override fun decode(buffer: PacketByteBuf) {
        slot = buffer.readSizedInt(IntSize.U_BYTE)
    }
}