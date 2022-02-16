package com.cablemc.pokemoncobbled.forge.common.net.messages.client.storage.party

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.FriendlyByteBuf
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

    override fun encode(buffer: FriendlyByteBuf) {
        buffer.writeUUID(storeID)
        buffer.writeUUID(pokemonID1)
        buffer.writeUUID(pokemonID2)
    }

    override fun decode(buffer: FriendlyByteBuf) {
        storeID = buffer.readUUID()
        pokemonID1 = buffer.readUUID()
        pokemonID2 = buffer.readUUID()
    }
}