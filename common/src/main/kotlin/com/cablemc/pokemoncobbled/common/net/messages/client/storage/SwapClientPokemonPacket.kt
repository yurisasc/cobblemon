package com.cablemc.pokemoncobbled.common.net.messages.client.storage

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Swaps two Pokémon in the client side representation of a store. Works for party and PCs.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.storage.SwapClientPokemonHandler].
 *
 * @author Hiroku
 * @since June 18th, 2022
 */
class SwapClientPokemonPacket() : NetworkPacket {
    var storeIsParty = false
    lateinit var storeID: UUID
    lateinit var pokemonID1: UUID
    lateinit var pokemonID2: UUID

    constructor(store: PokemonStore<*>, pokemonID1: UUID, pokemonID2: UUID): this() {
        this.storeIsParty = store is PartyStore
        this.storeID = store.uuid
        this.pokemonID1 = pokemonID1
        this.pokemonID2 = pokemonID2
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(storeIsParty)
        buffer.writeUuid(storeID)
        buffer.writeUuid(pokemonID1)
        buffer.writeUuid(pokemonID2)
    }

    override fun decode(buffer: PacketByteBuf) {
        storeIsParty = buffer.readBoolean()
        storeID = buffer.readUuid()
        pokemonID1 = buffer.readUuid()
        pokemonID2 = buffer.readUuid()
    }
}