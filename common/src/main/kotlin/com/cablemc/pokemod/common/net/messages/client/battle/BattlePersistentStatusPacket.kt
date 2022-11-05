package com.cablemc.pokemod.common.net.messages.client.battle

import com.cablemc.pokemod.common.api.net.NetworkPacket
import com.cablemc.pokemod.common.pokemon.status.PersistentStatus
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/**
 * Packet sent to change the status of a Pokémon in battle, such as paralysis or sleep.
 *
 * @author Hiroku
 * @since November 5th, 2022
 */
class BattlePersistentStatusPacket() : NetworkPacket {
    lateinit var pnx: String
    var status: Identifier? = null

    constructor(pnx: String, status: PersistentStatus?): this() {
        this.pnx = pnx
        this.status = status?.name
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeString(pnx)
        buffer.writeNullable(status) { buf, value -> buf.writeIdentifier(value)}
    }

    override fun decode(buffer: PacketByteBuf) {
        pnx = buffer.readString()
        status = buffer.readNullable(PacketByteBuf::readIdentifier)
    }
}