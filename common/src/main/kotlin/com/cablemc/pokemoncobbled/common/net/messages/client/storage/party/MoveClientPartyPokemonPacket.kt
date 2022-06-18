package com.cablemc.pokemoncobbled.common.net.messages.client.storage.party

import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.MoveClientPokemonPacket
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Moves a Pokémon from one party place to another on the client side.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.storage.party.MovePartyPokemonHandler]
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
class MoveClientPartyPokemonPacket() : MoveClientPokemonPacket<PartyPosition>() {
    constructor(storeID: UUID, pokemonID: UUID, newPosition: PartyPosition) : this() {
        this.storeID = storeID
        this.pokemonID = pokemonID
        this.newPosition = newPosition
    }

    override fun encodePosition(buffer: PacketByteBuf, position: PartyPosition) = buffer.writeSizedInt(IntSize.U_BYTE, position.slot)
    override fun decodePosition(buffer: PacketByteBuf) = PartyPosition(buffer.readSizedInt(IntSize.U_BYTE))
}