package com.cablemc.pokemoncobbled.common.net.messages.client

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import net.minecraft.network.FriendlyByteBuf
import java.util.UUID

/**
 * Base packet for all the single-field Pokémon update packets.
 *
 * @author Hiroku
 * @since November 28, 2021
 */
abstract class PokemonUpdatePacket : NetworkPacket {
    /** The UUID of the [PokemonStore] the Pokémon is in. */
    var storeID = UUID.randomUUID()
    /** The UUID of the [Pokemon] to update. */
    var pokemonID = UUID.randomUUID()

    fun setTarget(pokemon: Pokemon) {
        this.storeID = pokemon.storageCoordinates.get()!!.store.getUUID()
        this.pokemonID = pokemon.uuid
    }

    override fun encode(buffer: FriendlyByteBuf) {
        buffer.writeUUID(storeID)
        buffer.writeUUID(pokemonID)
    }

    override fun decode(buffer: FriendlyByteBuf) {
        storeID = buffer.readUUID()
        pokemonID = buffer.readUUID()
    }

    /** Applies the update to the located Pokémon. */
    abstract fun applyToPokemon(pokemon: Pokemon)
}