package com.cablemc.pokemoncobbled.common.net.messages.client.storage.party

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Removes a Pokémon from a particular store on the client side.
 *
 * Handled by [com.cablemc.pokemoncobbled.client.net.storage.party.RemovePartyPokemonHandler]
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
class RemovePartyPokemonPacket() : NetworkPacket {
    lateinit var storeID: UUID
    lateinit var pokemonID: UUID

    constructor(storeID: UUID, pokemonID: UUID): this() {
        this.storeID = storeID
        this.pokemonID = pokemonID
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(storeID)
        buffer.writeUuid(pokemonID)
    }

    override fun decode(buffer: PacketByteBuf) {
        storeID = buffer.readUuid()
        pokemonID = buffer.readUuid()
    }
}