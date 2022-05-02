package com.cablemc.pokemoncobbled.common.net.messages.client

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Base packet for all the single-field Pokémon update packets.
 *
 * @author Hiroku
 * @since November 28th, 2021
 */
abstract class PokemonUpdatePacket : NetworkPacket {
    /** The UUID of the [PokemonStore] the Pokémon is in. */
    var storeID = UUID.randomUUID()
    /** The UUID of the [Pokemon] to update. */
    var pokemonID = UUID.randomUUID()

    fun setTarget(pokemon: Pokemon) {
        this.storeID = pokemon.storeCoordinates.get()!!.store.uuid
        this.pokemonID = pokemon.uuid
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(storeID)
        buffer.writeUuid(pokemonID)
    }

    override fun decode(buffer: PacketByteBuf) {
        storeID = buffer.readUuid()
        pokemonID = buffer.readUuid()
    }

    /** Applies the update to the located Pokémon. */
    abstract fun applyToPokemon(pokemon: Pokemon)
}