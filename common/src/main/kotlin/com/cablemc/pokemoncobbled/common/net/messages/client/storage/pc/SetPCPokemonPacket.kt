package com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc

import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.SetPokemonPacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import io.netty.buffer.ByteBuf
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Sets a specific Pokémon in a specific slot of the client-side representation of a PC.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.storage.pc.SetPCPokemonHandler].
 *
 * @author Hiroku
 * @since June 18th, 2022
 */
class SetPCPokemonPacket() : SetPokemonPacket<PCPosition>() {
    constructor(storeID: UUID, storePosition: PCPosition, pokemon: Pokemon): this() {
        this.storeID = storeID
        this.storePosition = storePosition
        this.pokemon = pokemon
    }

    override fun encodePosition(buffer: PacketByteBuf): ByteBuf {
        buffer.writeSizedInt(IntSize.U_BYTE, storePosition.box)
        buffer.writeSizedInt(IntSize.U_BYTE, storePosition.slot)
        return buffer
    }

    override fun decodePosition(buffer: PacketByteBuf) = PCPosition(
        buffer.readSizedInt(IntSize.U_BYTE),
        buffer.readSizedInt(IntSize.U_BYTE)
    )
}