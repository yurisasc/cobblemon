package com.cablemc.pokemoncobbled.common.net.messages.client.storage.party

import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.SetPokemonPacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.network.FriendlyByteBuf
import java.util.UUID

/**
 * Adds the given Pokémon to a specific location in the client storage. This should be a new
 * Pokémon that the client doesn't know about yet.
 *
 * Handled by [com.cablemc.pokemoncobbled.client.net.storage.party.SetPartyPokemonHandler]
 *
 * @author Hiroku
 * @since November 29th, 2021
*/
class SetPartyPokemonPacket() : SetPokemonPacket<PartyPosition>() {
    constructor(storeID: UUID, storePosition: PartyPosition, pokemon: Pokemon): this() {
        this.storeID = storeID
        this.storePosition = storePosition
        this.pokemon = pokemon
    }

    override fun encodePosition(buffer: FriendlyByteBuf) = buffer.writeByte(storePosition.slot)
    override fun decodePosition(buffer: FriendlyByteBuf) = PartyPosition(buffer.readUnsignedByte().toInt())
}