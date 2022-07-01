package com.cablemc.pokemoncobbled.common.net.messages.server

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.BenchMoveHandler
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Tells the server to exchange a current move with a benched move in the specified Pokémon's
 * moveset. It can be used for PC and party Pokémon.
 *
 * It should probably be split into two packets for which store type it's targeting, or include the store
 * position in an abstract way so that the PC case doesn't have to scavenge through the entire PC.
 *
 * Handled by [BenchMoveHandler].
 *
 * @author Hiroku
 * @since April 18th, 2022
 */
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