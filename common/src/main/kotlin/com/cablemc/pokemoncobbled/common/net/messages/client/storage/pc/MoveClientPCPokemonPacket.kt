package com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc

import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.MoveClientPokemonPacket
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Moves a Pokémon from one part of a PC to another on the client side.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.storage.pc.MoveClientPCPokemonHandler].
 *
 * @author Hiroku
 * @since June 18th, 2022
 */
class MoveClientPCPokemonPacket() : MoveClientPokemonPacket<PCPosition>() {
    constructor(storeID: UUID, pokemonID: UUID, newPosition: PCPosition) : this() {
        this.storeID = storeID
        this.pokemonID = pokemonID
        this.newPosition = newPosition
    }

    override fun encodePosition(buffer: PacketByteBuf, position: PCPosition) {
        buffer.writeSizedInt(IntSize.U_BYTE, position.box)
        buffer.writeSizedInt(IntSize.U_BYTE, position.slot)
    }

    override fun decodePosition(buffer: PacketByteBuf) = PCPosition(
        box = buffer.readSizedInt(IntSize.U_BYTE),
        slot = buffer.readSizedInt(IntSize.U_BYTE)
    )
}