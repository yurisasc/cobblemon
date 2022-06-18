package com.cablemc.pokemoncobbled.common.net.messages.client.storage

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Removes a Pokémon from a particular store on the client side, working for both parties and PCs.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.storage.RemoveClientPokemonHandler]
 *
 * @author Hiroku
 * @since June 18th, 2022
 */
class RemoveClientPokemonPacket() : NetworkPacket {
    var storeIsParty = false
    lateinit var storeID: UUID
    lateinit var pokemonID: UUID

    constructor(store: PokemonStore<*>, pokemonID: UUID): this() {

        this.storeIsParty = store is PartyStore
        this.storeID = store.uuid
        this.pokemonID = pokemonID
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(storeIsParty)
        buffer.writeUuid(storeID)
        buffer.writeUuid(pokemonID)
    }

    override fun decode(buffer: PacketByteBuf) {
        this.storeIsParty = buffer.readBoolean()
        this.storeID = buffer.readUuid()
        this.pokemonID = buffer.readUuid()
    }
}