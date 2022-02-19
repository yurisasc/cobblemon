package com.cablemc.pokemoncobbled.common.net.messages.client.storage.party

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import net.minecraft.network.FriendlyByteBuf
import java.util.UUID

/**
 * Moves a Pokémon from one place to another on the client side.
 *
 * Handled by [com.cablemc.pokemoncobbled.client.net.storage.party.MovePartyPokemonHandler]
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
class MovePartyPokemonPacket() : NetworkPacket {
    lateinit var storeID: UUID
    lateinit var pokemonID: UUID
    lateinit var newPosition: PartyPosition

    constructor(storeID: UUID, pokemonID: UUID, newPosition: PartyPosition): this() {
        this.storeID = storeID
        this.pokemonID = pokemonID
        this.newPosition = newPosition
    }

    override fun encode(buffer: FriendlyByteBuf) {
        buffer.writeUUID(storeID)
        buffer.writeUUID(pokemonID)
        buffer.writeByte(newPosition.slot)
    }

    override fun decode(buffer: FriendlyByteBuf) {
        storeID = buffer.readUUID()
        pokemonID = buffer.readUUID()
        newPosition = PartyPosition(buffer.readUnsignedByte().toInt())
    }
}