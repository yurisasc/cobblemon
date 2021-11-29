package com.cablemc.pokemoncobbled.common.net.messages.client.storage.party

import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.SetPokemonPacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.network.FriendlyByteBuf
import java.util.UUID

class SetPartyPokemonPacket() : SetPokemonPacket<PartyPosition>() {
    constructor(storeID: UUID, storePosition: PartyPosition, pokemon: Pokemon): this() {
        this.storeID = storeID
        this.storePosition = storePosition
        this.pokemon = pokemon
    }

    override fun encodePosition(buffer: FriendlyByteBuf) = buffer.writeByte(storePosition.slot)
    override fun decodePosition(buffer: FriendlyByteBuf) = PartyPosition(buffer.readUnsignedByte().toInt())
}