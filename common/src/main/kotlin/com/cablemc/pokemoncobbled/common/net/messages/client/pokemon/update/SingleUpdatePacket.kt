package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.net.messages.client.PokemonUpdatePacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.network.PacketByteBuf

/**
 * Base class for packets which update a single value of a Pokémon.
 *
 * Handled by [com.cablemc.pokemoncobbled.client.net.pokemon.update.SingleUpdatePacketHandler]
 *
 * @author Hiroku
 * @since November 28th, 2021
 */
abstract class SingleUpdatePacket<T>(var value: T) : PokemonUpdatePacket() {
    override fun encode(buffer: PacketByteBuf) {
        super.encode(buffer)
        encodeValue(buffer, value)
    }

    override fun decode(buffer: PacketByteBuf) {
        super.decode(buffer)
        value = decodeValue(buffer)
    }

    override fun applyToPokemon(pokemon: Pokemon) {
        set(pokemon, value)
    }

    abstract fun encodeValue(buffer: PacketByteBuf, value: T)
    abstract fun decodeValue(buffer: PacketByteBuf): T

    /** Sets the value in the client-side Pokémon. */
    abstract fun set(pokemon: Pokemon, value: T)
}