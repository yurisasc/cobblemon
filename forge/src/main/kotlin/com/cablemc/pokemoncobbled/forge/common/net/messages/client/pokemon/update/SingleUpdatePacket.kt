package com.cablemc.pokemoncobbled.forge.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.forge.common.net.messages.client.PokemonUpdatePacket
import com.cablemc.pokemoncobbled.forge.common.pokemon.Pokemon
import net.minecraft.network.FriendlyByteBuf

/**
 * Base class for packets which update a single value of a Pokémon.
 *
 * Handled by [com.cablemc.pokemoncobbled.client.net.pokemon.update.SingleUpdatePacketHandler]
 *
 * @author Hiroku
 * @since November 28th, 2021
 */
abstract class SingleUpdatePacket<T>(var value: T) : PokemonUpdatePacket() {
    override fun encode(buffer: FriendlyByteBuf) {
        super.encode(buffer)
        encodeValue(buffer, value)
    }

    override fun decode(buffer: FriendlyByteBuf) {
        super.decode(buffer)
        value = decodeValue(buffer)
    }

    override fun applyToPokemon(pokemon: Pokemon) {
        set(pokemon, value)
    }

    abstract fun encodeValue(buffer: FriendlyByteBuf, value: T)
    abstract fun decodeValue(buffer: FriendlyByteBuf): T

    /** Sets the value in the client-side Pokémon. */
    abstract fun set(pokemon: Pokemon, value: T)
}