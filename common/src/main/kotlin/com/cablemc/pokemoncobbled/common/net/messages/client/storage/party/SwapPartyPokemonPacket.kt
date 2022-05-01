package com.cablemc.pokemoncobbled.common.net.messages.client.storage.party

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Swaps two Pokémon with each other within the same store on the client side.
 *
 * Handled by [com.cablemc.pokemoncobbled.client.net.storage.party.SwapPartyPokemonHandler]
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
class SwapPartyPokemonPacket() : NetworkPacket {
    lateinit var storeID: UUID
    lateinit var pokemonID1: UUID
    lateinit var pokemonID2: UUID

    constructor(storeID: UUID, pokemonID1: UUID, pokemonID2: UUID): this() {
        this.storeID = storeID
        this.pokemonID1 = pokemonID1
        this.pokemonID2 = pokemonID2
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(storeID)
        buffer.writeUuid(pokemonID1)
        buffer.writeUuid(pokemonID2)
    }

    override fun decode(buffer: PacketByteBuf) {
        storeID = buffer.readUuid()
        pokemonID1 = buffer.readUuid()
        pokemonID2 = buffer.readUuid()
    }
}