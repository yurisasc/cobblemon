package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.net.messages.client.PokemonUpdatePacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.network.FriendlyByteBuf

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
    abstract fun set(pokemon: Pokemon, value: T)
}